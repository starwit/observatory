package de.starwit.service.jobs;

import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.InstantSource;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.analytics.AreaOccupancyService;
import de.starwit.service.geojson.GeoJsonService;
import de.starwit.service.observatory.ObservationJobService;
import de.starwit.service.sae.SaeDetectionDto;
import de.starwit.service.sae.SaeMessageListener;
import jakarta.annotation.PostConstruct;

@Service
public class AreaOccupancyRunner {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${sae.redisStreamPrefix:output}")
    private String REDIS_STREAM_PREFIX;
    
    @Value("${sae.areaOccupancy.analyzingInterval:10s}")
    private Duration ANALYZING_INTERVAL;
    
    @Value("${sae.areaOccupancy.geoDistanceP95Threshold:0.001}")
    private double GEO_DISTANCE_P95_THRESHOLD;
    
    @Value("${sae.areaOccupancy.pxDistanceP95ThresholdScale:0.1}")
    private double PX_DISTANCE_P95_THRESHOLD_SCALE;

    @Autowired
    private ObservationJobService observationJobService;

    @Autowired
    private AreaOccupancyService areaOccupancyService;

    @Autowired
    private GeoJsonService geoJsonService;

    @Autowired
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamListenerContainer;

    private SaeMessageListener saeMessageListener;
    
    // This is to facilitate reproducible testing
    private InstantSource instantSource;

    private List<Subscription> activeSubscriptions = new ArrayList<>();
    private List<AreaOccupancyJob> activeJobs = new ArrayList<>();

    public AreaOccupancyRunner(InstantSource instantSource, ScheduledExecutorService executorService) {
        this.instantSource = instantSource;
        this.saeMessageListener = new SaeMessageListener(this::messageHandler);
    }
    
    public AreaOccupancyRunner() {
        this(InstantSource.system(), Executors.newSingleThreadScheduledExecutor());
    }

    @PostConstruct
    private void init() {
        streamListenerContainer.start();
        refreshJobs();
    }

    public void refreshJobs() {
        log.debug("Refreshing jobs");

        for (Subscription activeSub : activeSubscriptions) {
            streamListenerContainer.remove(activeSub);
        }
        
        this.activeSubscriptions = new ArrayList<>();

        List<ObservationJobEntity> enabledJobEntites = observationJobService.findActiveAreaOccupancyJobs();
        log.info("Enabled jobs: " + enabledJobEntites.stream().map(j -> j.getName()).collect(Collectors.joining(",")));

        this.activeJobs = enabledJobEntites.stream().map(jobEntity -> new AreaOccupancyJob(jobEntity, ANALYZING_INTERVAL)).toList();

        for (String streamId : enabledJobEntites.stream().map(e -> e.getCameraId()).distinct().toList()) {
            String streamKey = REDIS_STREAM_PREFIX + ":" + streamId;
            log.info("Subscribing to " + streamKey);
            StreamOffset<String> streamOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());
            Subscription redisSubscription = streamListenerContainer.receive(streamOffset, saeMessageListener);
            activeSubscriptions.add(redisSubscription);
        }
    }

    public void messageHandler(SaeDetectionDto dto) {
        for (AreaOccupancyJob job : activeJobs) {
            if (job.getConfigEntity().getCameraId().equals(dto.getCameraId())) {
                addDetection(job, dto);
            }
        }
    }

    public void addDetection(AreaOccupancyJob job, SaeDetectionDto dto) {
        job.getTrajectoryStore().addDetection(dto);
        job.setLastUpdate(instantSource.instant());
    }

    @Scheduled(fixedRate = 5000)
    private void runJobs() {
        for (AreaOccupancyJob job : activeJobs) {
            runJob(job);
        }
    }
    
    public void runJob(AreaOccupancyJob job) {
        // Skip processing if we have not received any new data within the last analyzing interval
        if (job.getLastUpdate().isBefore(instantSource.instant().minus(job.getAnalyzingInterval()))) {
            log.warn("Skipping processing due to stale data (" + job.getConfigEntity().getName() + ")");
            return;
        }
        
        long objectCount = 0;
        List<List<SaeDetectionDto>> trajectories = job.getTrajectoryStore().getAllValidTrajectories();
        
        for (List<SaeDetectionDto> trajectory : trajectories) {
            List<Point2D> pointTrajectory = GeometryConverter.toCenterPoints(trajectory, job.getConfigEntity().getGeoReferenced());
            Point2D avgPos = getAveragePosition(pointTrajectory);

            if (!job.getPolygon().contains(avgPos)) {
                continue;
            }

            // Use bounding box size as stationarity constraint if not geo-referenced (to compensate for perspective)
            boolean stationary = false;
            if (job.getConfigEntity().getGeoReferenced()) {
                stationary = isStationary(pointTrajectory, GEO_DISTANCE_P95_THRESHOLD);
            } else {
                stationary = isStationary(pointTrajectory, getAverageBoundingBoxDiagonal(trajectory) * PX_DISTANCE_P95_THRESHOLD_SCALE);
            }

            if (stationary) {
                objectCount++;
                log.debug("Stationary " + trajectory.get(0).getObjectId().substring(0, 4));
            }
        }
        
        job.getTrajectoryStore().purge();

        storeObservation(new AreaOccupancyObservation(job.getConfigEntity(), job.getTrajectoryStore().getMostRecentTimestamp().atZone(ZoneOffset.UTC), objectCount));
    }

    /**
     * Determines if the passed trajectory meets our stationary position requirements.
     * Right now that means the 95 percentile of distances to the average position (within the recorded track) is smaller than a certain threshold.
     * @param pointTrajectory
     * @return
     */
    private boolean isStationary(List<Point2D> pointTrajectory, double threshold) {
        Point2D avgPos = getAveragePosition(pointTrajectory);

        double[] distances = pointTrajectory.stream().map(p -> p.distance(avgPos)).mapToDouble(d -> d).toArray();
        double distanceP95 = new Percentile().evaluate(distances, 0.95);

        boolean stationary = distanceP95 < threshold;

        log.debug("trajLen " + String.format("%04d", pointTrajectory.size()) + ", distance P95: " + String.format("%10.8f", distanceP95) + ", threshold: " + String.format("%10.8f", threshold));
    
        return stationary;
    }

    private Point2D getAveragePosition(List<Point2D> pointTrajectory) {
        double avgX = pointTrajectory.stream().mapToDouble(p -> p.getX()).summaryStatistics().getAverage();
        double avgY = pointTrajectory.stream().mapToDouble(p -> p.getY()).summaryStatistics().getAverage();

        return new Point2D.Double(avgX, avgY);
    }

    private double getAverageBoundingBoxDiagonal(List<SaeDetectionDto> dtos) {
        double avgDeltaX = dtos.stream().mapToDouble(d -> d.getMaxX() - d.getMinX()).summaryStatistics().getAverage();
        double avgDeltaY = dtos.stream().mapToDouble(d -> d.getMaxY() - d.getMinY()).summaryStatistics().getAverage();

        return Math.sqrt(Math.pow(avgDeltaX, 2) + Math.pow(avgDeltaY, 2));
    }

    private void storeObservation(AreaOccupancyObservation obs) {
        areaOccupancyService.addEntry(obs.jobEntity(), obs.occupancyTime(), obs.count());
        geoJsonService.sendAreaOccupancies(Arrays.asList(obs));
    }

}
