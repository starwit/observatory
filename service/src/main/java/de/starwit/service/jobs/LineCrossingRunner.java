package de.starwit.service.jobs;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.stereotype.Service;

import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.analytics.LineCrossingService;
import de.starwit.service.geojson.GeoJsonService;
import de.starwit.service.observatory.ObservationJobService;
import de.starwit.service.sae.SaeDetectionDto;
import de.starwit.service.sae.SaeMessageListener;
import jakarta.annotation.PostConstruct;

@Service
public class LineCrossingRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${sae.lineCrossing.targetWindowSize:1s}")
    private Duration TARGET_WINDOW_SIZE;    

    @Value("${sae.redisStreamPrefix:output}")
    private String REDIS_STREAM_PREFIX;

    @Autowired
    private LineCrossingService lineCrossingService;

    @Autowired
    private ObservationJobService observationJobService;

    @Autowired
    private GeoJsonService geoJsonService;

    @Autowired
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamListenerContainer;

    private SaeMessageListener saeMessageListener;

    private List<Subscription> activeSubscriptions = new ArrayList<>();
    private List<LineCrossingJob> activeJobs = new ArrayList<>();

    private LineCrossingJob currentJob;

    public LineCrossingRunner() {
        this.saeMessageListener = new SaeMessageListener(this::messageHandler);
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

        List<ObservationJobEntity> enabledJobEntites = observationJobService.findActiveLineCrossingJobs();
        log.info("Enabled jobs: " + enabledJobEntites.stream().map(j -> j.getName()).collect(Collectors.joining(",")));

        this.activeJobs = enabledJobEntites.stream().map(jobEntity -> new LineCrossingJob(jobEntity, TARGET_WINDOW_SIZE)).toList();

        for (String streamId : enabledJobEntites.stream().map(e -> e.getCameraId()).distinct().toList()) {
            String streamKey = REDIS_STREAM_PREFIX + ":" + streamId;
            log.info("Subscribing to " + streamKey);
            StreamOffset<String> streamOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());
            Subscription redisSubscription = streamListenerContainer.receive(streamOffset, saeMessageListener);
            activeSubscriptions.add(redisSubscription);
        }
    }
    
    public void messageHandler(SaeDetectionDto dto) {
        for (LineCrossingJob lineJob : activeJobs) {
            if (lineJob.getConfigEntity().getCameraId().equals(dto.getCameraId())) {
                processNewDetection(lineJob, dto);
            }
        }
    }

    protected void processNewDetection(LineCrossingJob job, SaeDetectionDto dto) {
        this.currentJob = job;

        TrajectoryStore trajectoryStore = job.getTrajectoryStore();
        log.debug("store size: {}", trajectoryStore.size());

        trajectoryStore.addDetection(dto);
        trimTrajectory(dto);
        if (isTrajectoryValid(dto)) {
            if (objectHasCrossed(dto)) {
                storeObservation(new LineCrossingObservation(dto, getCrossingDirection(dto), job.getConfigEntity()));
                trajectoryStore.clear(dto);
            }
        }

        trajectoryStore.purge(dto.getCaptureTs());
    }
    
    private void trimTrajectory(SaeDetectionDto det) {
        TrajectoryStore trajectoryStore = currentJob.getTrajectoryStore();

        Instant trajectoryEnd = trajectoryStore.getLast(det).getCaptureTs();

        boolean trimming = true;
        while (trimming) {
            Instant trajectoryStart = trajectoryStore.getFirst(det).getCaptureTs();
            if (Duration.between(trajectoryStart, trajectoryEnd).toMillis() > TARGET_WINDOW_SIZE.toMillis()) {
                trajectoryStore.removeFirst(det);
            } else {
                trimming = false;
            }
        }
    }

    private boolean isTrajectoryValid(SaeDetectionDto det) {
        TrajectoryStore trajectoryStore = currentJob.getTrajectoryStore();

        Instant trajectoryStart = trajectoryStore.getFirst(det).getCaptureTs();
        Instant trajectoryEnd = trajectoryStore.getLast(det).getCaptureTs();
        return Duration.between(trajectoryStart, trajectoryEnd).toMillis() > 0.8 * TARGET_WINDOW_SIZE.toMillis();
    }

    private boolean objectHasCrossed(SaeDetectionDto det) {
        TrajectoryStore trajectoryStore = currentJob.getTrajectoryStore();
        Line2D countingLine = currentJob.getCountingLine();
        
        Point2D firstPoint = GeometryConverter.toCenterPoint(trajectoryStore.getFirst(det), currentJob.isGeoReferenced());
        Point2D lastPoint = GeometryConverter.toCenterPoint(trajectoryStore.getLast(det), currentJob.isGeoReferenced());
        Line2D trajectory = new Line2D.Double(firstPoint, lastPoint);
        return trajectory.intersectsLine(countingLine);
    }
    
    private Direction getCrossingDirection(SaeDetectionDto det) {
        TrajectoryStore trajectoryStore = currentJob.getTrajectoryStore();
        Line2D countingLine = currentJob.getCountingLine();

        Point2D trajectoryEnd = GeometryConverter.toCenterPoint(trajectoryStore.getLast(det), currentJob.isGeoReferenced());
        int ccw = countingLine.relativeCCW(trajectoryEnd);
        if (ccw == -1) {
            return Direction.out;
        } else {
            return Direction.in;
        }
    }

    private void storeObservation(LineCrossingObservation obs) {
        lineCrossingService.addEntry(obs.det(), obs.direction(), obs.jobEntity());
        geoJsonService.sendLineCrossings(Arrays.asList(obs));
    }

}
