package de.starwit.service.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    
    @Value("${areaOccupancy.analyzingWindow:10s}")
    private Duration ANALYZING_WINDOW;

    @Value("${areaOccupancy.geoDistanceP95Threshold:0.0001}")
    private double GEO_DISTANCE_P95_THRESHOLD;
    
    @Value("${areaOccupancy.pxDistanceP95ThresholdScale:0.1}")
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
    
    private List<Subscription> activeSubscriptions = new ArrayList<>();
    private List<AreaOccupancyJob> activeJobs = new ArrayList<>();

    public AreaOccupancyRunner() {
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

        List<ObservationJobEntity> enabledJobEntites = observationJobService.findActiveAreaOccupancyJobs();
        log.info("Enabled jobs: " + enabledJobEntites.stream().map(j -> j.getName()).collect(Collectors.joining(",")));

        this.activeJobs = enabledJobEntites.stream().map(jobEntity -> new AreaOccupancyJob(jobEntity, ANALYZING_WINDOW, GEO_DISTANCE_P95_THRESHOLD, PX_DISTANCE_P95_THRESHOLD_SCALE, this::storeObservation)).toList();

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
                job.addDetection(dto, Instant.now());
            }
        }
    }

    // TODO: Change this back to simple duration format ("5s") after upgrading to Spring Boot 3.4.x
    @Scheduled(fixedRateString = "${areaOccupancy.analyzingIntervalMs:5000}", timeUnit = TimeUnit.MILLISECONDS)
    private void runJobs() {
        for (AreaOccupancyJob job : activeJobs) {
            // Skip processing if we have not received any new data within the last analyzing interval
            if (job.getLastUpdate().isBefore(Instant.now().minus(job.getAnalyzingInterval()))) {
                log.warn("Skipping processing due to stale data (" + job.getConfigEntity().getName() + ")");
                continue;
            }

            job.run();
        }
    }

    private void storeObservation(AreaOccupancyObservation obs) {
        areaOccupancyService.addEntry(obs.jobEntity(), obs.occupancyTime(), obs.count());
        geoJsonService.sendAreaOccupancies(Arrays.asList(obs));
    }

}
