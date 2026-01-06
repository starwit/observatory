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
import org.springframework.stereotype.Component;

import de.starwit.persistence.observatory.entity.JobType;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.analytics.AreaOccupancyService;
import de.starwit.service.analytics.LineCrossingService;
import de.starwit.service.jobs.JobInterface;
import de.starwit.service.jobs.RunnerInterface;
import de.starwit.service.jobs.linecrossing.LineCrossingJob;
import de.starwit.service.jobs.linecrossing.LineCrossingObservation;
import de.starwit.service.observatory.ObservationJobService;
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
    private LineCrossingService lineCrossingService;

    @Autowired
    private ObservationJobService observationJobService;

    @Autowired
    private AreaOccupancyService areaOccupancyService;

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
        List<ObservationJobEntity> enabledJobEntites = observationJobService.findActiveJobs(JOB_TYPE);
        log.info("Enabled jobs: " + enabledJobEntites.stream().map(j -> j.getName()).collect(Collectors.joining(",")));

        this.activeJobs = enabledJobEntites.stream()
                .map(jobEntity -> new LineCrossingJob(jobEntity, TARGET_WINDOW_SIZE, this::storeObservation)).toList();
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
