package de.starwit.service.jobs.areaoccupancy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
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
import de.starwit.service.geojson.GeoJsonService;
import de.starwit.service.jobs.JobInterface;
import de.starwit.service.jobs.RunnerInterface;
import de.starwit.service.observatory.ObservationJobService;
import jakarta.annotation.PostConstruct;

@Component
public class AreaOccupancyRunner implements RunnerInterface {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public static final JobType JOB_TYPE = JobType.AREA_OCCUPANCY;

    @Value("${sae.redisStreamPrefix:output}")
    private String REDIS_STREAM_PREFIX;

    @Value("${areaOccupancy.analyzingWindow:5s}")
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

    private List<AreaOccupancyJob> activeJobs = new ArrayList<>();

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
        log.debug("Refreshing jobs");

        List<ObservationJobEntity> enabledJobEntites = observationJobService.findActiveJobs(JOB_TYPE);
        log.info("Enabled jobs: " + enabledJobEntites.stream().map(j -> j.getName()).collect(Collectors.joining(",")));

        this.activeJobs = enabledJobEntites.stream().map(jobEntity -> new AreaOccupancyJob(jobEntity, ANALYZING_WINDOW,
                GEO_DISTANCE_P95_THRESHOLD, PX_DISTANCE_P95_THRESHOLD_SCALE, this::storeObservation)).toList();
    }

    private void storeObservation(AreaOccupancyObservation obs) {
        try {
            areaOccupancyService.addEntry(obs.jobEntity(), obs.occupancyTime(), obs.count());
            geoJsonService.sendAreaOccupancies(Arrays.asList(obs));
        } catch (Exception e) {
            log.error("Error storing AreaOccupancy observation: (area={}, name={}) with count {} at time {}",
                    obs.jobEntity().getObservationAreaId(), obs.jobEntity().getName(), obs.count(), obs.occupancyTime(),
                    e);
        }
    }

}
