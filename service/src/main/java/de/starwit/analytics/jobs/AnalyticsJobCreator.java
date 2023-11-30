package de.starwit.analytics.jobs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.starwit.persistence.databackendconfig.entity.AnalyticsJobEntity;
import de.starwit.service.impl.AnalyticsJobService;

@Component
public class AnalyticsJobCreator {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${analytics.dataRetrievalRate:2000}")
    private int dataRetrievalRate;

    private List<JobData> jobsToRun = null;

    @Autowired
    private AnalyticsJobService analyticsJobService;

    @Autowired
    private AreaOccupancyJob areaOccupancyJob;

    @Autowired
    private LineCrossingJob lineCrossingJob;

    // @Scheduled(initialDelay = 0, fixedRate = 1000)
    private void refreshJobs() {
        log.info("in refreshJobs");
        jobsToRun = new ArrayList<>();
        List<AnalyticsJobEntity> enabledJobs = analyticsJobService.findByEnabledTrue();
        for (AnalyticsJobEntity jobConfig : enabledJobs) {
            jobsToRun.add(new JobData(jobConfig));
        }
    }

    @Scheduled(initialDelay = 1000, fixedRate = 10000)
    private void runJobs() {
        log.info("in runJobs");
        refreshJobs();
        try {
            if (jobsToRun != null && !jobsToRun.isEmpty()) {
                for (JobData job : jobsToRun) {
                    switch (job.getConfig().getType()) {
                        case LINE_CROSSING:
                            lineCrossingJob.getAndProcessNewData(job);
                            break;
                        case AREA_OCCUPANCY:
                            areaOccupancyJob.getAndProcessNewData(job);
                            break;
                        default:
                            break;
                    }
                    log.debug("Running job: {}", job.getConfig().getName());
                }
            }
        } catch (Exception e) {
            log.error("Exception during running the job", e);
        }
    }
}
