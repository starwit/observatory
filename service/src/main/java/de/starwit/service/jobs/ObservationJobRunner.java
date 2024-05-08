package de.starwit.service.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamReadRequest;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamReadRequestBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.service.analytics.AreaOccupancyService;
import de.starwit.service.analytics.LineCrossingService;
import de.starwit.service.databackend.ObservationJobService;
import de.starwit.service.geojson.GeoJsonMapper;
import de.starwit.service.geojson.GeoJsonService;
import de.starwit.service.sae.SaeDetectionDto;
import de.starwit.service.sae.SaeMessageListener;
import jakarta.annotation.PostConstruct;

@Component
public class ObservationJobRunner {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${sae.redisStreamPrefix:output}")
    private String REDIS_STREAM_PREFIX;

    private List<Subscription> activeSubscriptions = new ArrayList<>();
    private List<AbstractJob> activeJobs = new ArrayList<>();

    private SaeMessageListener saeMessageListener = new SaeMessageListener();
    private LineCrossingObservationListener lineCrossingObservationListener = new LineCrossingObservationListener();
    private AreaOccupancyObservationListener areaOccupancyObservationListener = new AreaOccupancyObservationListener();

    @Lazy
    @Autowired
    private ObservationJobService observationJobService;

    @Autowired
    private LineCrossingService lineCrossingService;

    @Autowired
    private AreaOccupancyService areaOccupancyService;

    @Autowired
    private GeoJsonService geoJsonService;

    @Autowired
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamListenerContainer;

    @PostConstruct
    private void init() {
        refreshJobs();
    }

    public void refreshJobs() {
        log.debug("Refreshing jobs");

        streamListenerContainer.stop();

        for (Subscription activeSub : activeSubscriptions) {
            streamListenerContainer.remove(activeSub);
        }

        this.activeSubscriptions = new ArrayList<>();
        this.activeJobs = new ArrayList<>();

        List<ObservationJobEntity> enabledJobEntities = observationJobService.findByEnabledTrue();

        for (ObservationJobEntity jobEntity : enabledJobEntities) {
            AbstractJob job = switch (jobEntity.getType()) {
                case AREA_OCCUPANCY -> new AreaOccupancyJob(jobEntity, areaOccupancyObservationListener);
                case LINE_CROSSING -> new LineCrossingJob(jobEntity, lineCrossingObservationListener);
            };
            activeJobs.add(job);
        }

        for (String streamId : enabledJobEntities.stream().map(e -> e.getCameraId()).distinct().toList()) {
            String streamKey = REDIS_STREAM_PREFIX + ":" + streamId;
            StreamOffset<String> streamOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());
            Subscription redisSubscription = streamListenerContainer.receive(streamOffset, saeMessageListener);
            activeSubscriptions.add(redisSubscription);
        }

        streamListenerContainer.start();        
    }
            
    @Scheduled(fixedDelay = 500, timeUnit = TimeUnit.MILLISECONDS)
    public void feedJobs() {
        List<SaeDetectionDto> newDtos = saeMessageListener.getBufferedMessages();
        for (SaeDetectionDto dto : newDtos) {
            for (AbstractJob job : activeJobs) {
                if (job.getConfigEntity().getCameraId().equals(dto.getCameraId())) {
                    job.pushNewDetection(dto);
                }
            }
        }
    }

    @Scheduled(fixedDelay = 500, timeUnit = TimeUnit.MILLISECONDS)
    public void storeObservations() {
        List<AreaOccupancyObservation> areaOccupancyObservations = areaOccupancyObservationListener.getBufferedMessages();
        areaOccupancyObservations.forEach(obs -> areaOccupancyService.addEntry(obs.jobEntity(), obs.occupancyTime(), obs.count()));
        
        List<LineCrossingObservation> lineCrossingObservations = lineCrossingObservationListener.getBufferedMessages();
        lineCrossingObservations.forEach(obs -> lineCrossingService.addEntry(obs.det(), obs.direction(), obs.jobEntity()));

        if (!areaOccupancyObservations.isEmpty()) {
            geoJsonService.sendGeoJson(GeoJsonMapper.mapAreaOccupancies(areaOccupancyObservations));
        }

        if (!lineCrossingObservations.isEmpty()) {
            geoJsonService.sendGeoJson(GeoJsonMapper.mapLineCrossings(lineCrossingObservations));
        }
    }
}
