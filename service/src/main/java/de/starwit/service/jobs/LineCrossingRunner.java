package de.starwit.service.jobs;

import java.time.Duration;
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

        this.activeJobs = enabledJobEntites.stream().map(jobEntity -> new LineCrossingJob(jobEntity, TARGET_WINDOW_SIZE, this::storeObservation)).toList();

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
                lineJob.processNewDetection(dto);
            }
        }
    }

    private void storeObservation(LineCrossingObservation obs) {
        lineCrossingService.addEntry(obs.det(), obs.direction(), obs.jobEntity());
        geoJsonService.sendLineCrossings(Arrays.asList(obs));
    }

}
