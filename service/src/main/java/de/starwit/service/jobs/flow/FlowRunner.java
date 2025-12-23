package de.starwit.service.jobs.flow;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties.Simple;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import de.starwit.persistence.observatory.entity.JobType;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.analytics.AreaOccupancyService;
import de.starwit.service.analytics.LineCrossingService;
import de.starwit.service.jobs.JobInterface;
import de.starwit.service.jobs.RunnerInterface;
import de.starwit.service.jobs.linecrossing.LineCrossingJob;
import de.starwit.service.jobs.linecrossing.LineCrossingObservation;
import de.starwit.service.observatory.ObservationJobService;
import de.starwit.service.sae.SaeMessageListener;
import de.starwit.service.sae.SimpleMessageListener;
import jakarta.annotation.PostConstruct;

@Component
public class FlowRunner implements RunnerInterface {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final JobType JOB_TYPE = JobType.FLOW;

    @Value("${lineCrossing.targetWindowSize:1s}")
    private Duration TARGET_WINDOW_SIZE;

    @Value("${sae.redisStreamPrefix:output}")
    private String REDIS_STREAM_PREFIX;

    @Autowired
    private SimpleMessageListener listener;

    @Autowired
    private LineCrossingService lineCrossingService;

    @Autowired
    private ObservationJobService observationJobService;

    @Autowired
    private AreaOccupancyService areaOccupancyService;

    @Autowired
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamListenerContainer;

    private List<Subscription> activeSubscriptions = new ArrayList<>();
    private List<LineCrossingJob> activeJobs = new ArrayList<>();

    @PostConstruct
    private void init() {
        refreshJobs();
    }

    @Override
    public List<? extends JobInterface> getActiveJobs() {
        return this.activeJobs;
    }

    @Override
    public void refreshJobs() {
        log.info("Refreshing jobs");

        for (Subscription activeSub : activeSubscriptions) {
            streamListenerContainer.remove(activeSub);
        }

        this.activeSubscriptions = new ArrayList<>();

        List<ObservationJobEntity> enabledJobEntites = observationJobService.findActiveJobs(JOB_TYPE);
        log.info("Enabled jobs: " + enabledJobEntites.stream().map(j -> j.getName()).collect(Collectors.joining(",")));

        this.activeJobs = enabledJobEntites.stream()
                .map(jobEntity -> new LineCrossingJob(jobEntity, TARGET_WINDOW_SIZE, this::storeObservation)).toList();

        for (String streamId : enabledJobEntites.stream().map(e -> e.getCameraId()).distinct().toList()) {
            String streamKey = REDIS_STREAM_PREFIX + ":" + streamId;
            log.info("Subscribing to " + streamKey);
            StreamOffset<String> streamOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());
            Subscription redisSubscription = streamListenerContainer.receive(streamOffset, listener);
            activeSubscriptions.add(redisSubscription);
        }
    }

    private void storeObservation(LineCrossingObservation obs) {
        try {
            lineCrossingService.addEntry(obs.det(), obs.direction(), obs.jobEntity());
            areaOccupancyService.updateCountFromFlow(obs.jobEntity(), obs.det().getCaptureTs().atZone(ZoneOffset.UTC),
                    obs.direction());
        } catch (Exception e) {
            log.error("Error storing flow observation in direction {} of class {} in area (area={}, name={})",
                    obs.direction(), obs.jobEntity().getDetectionClassId(), obs.jobEntity().getObservationAreaId(),
                    obs.jobEntity().getName(), e);
        }
    }

}
