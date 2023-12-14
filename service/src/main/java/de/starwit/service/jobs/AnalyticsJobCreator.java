package de.starwit.service.jobs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;
import de.starwit.service.databackend.AnalyticsJobService;

@Component
public class AnalyticsJobCreator {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private List<JobData> jobsToRun = null;

    @Autowired
    private AnalyticsJobService analyticsJobService;

    @Autowired
    private AreaOccupancyJob areaOccupancyJob;

    @Autowired
    private LineCrossingJob lineCrossingJob;

    @Scheduled(initialDelay = 0, fixedRate = 10000)
    private void refreshJobs() {
        log.debug("Refreshing jobs");
        jobsToRun = new ArrayList<>();
        List<AnalyticsJobEntity> enabledJobs = analyticsJobService.findByEnabledTrue();
        for (AnalyticsJobEntity jobConfig : enabledJobs) {
            jobsToRun.add(new JobData(jobConfig));
        }
    }

    @Scheduled(initialDelay = 1000, fixedRateString = "${analytics.jobRunInterval:10000}")
    private void runJobs() {
        if (jobsToRun != null && !jobsToRun.isEmpty()) {
            for (JobData job : jobsToRun) {
                try {
                    log.debug("Running job: {}", job.getConfig().getName());
                    switch (job.getConfig().getType()) {
                        case LINE_CROSSING:
                            lineCrossingJob.run(job);
                            break;
                        case AREA_OCCUPANCY:
                            areaOccupancyJob.run(job);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    log.error("Exception during job run {}", job.getConfig().getName(), e);
                }
            }
        }
    }
}
