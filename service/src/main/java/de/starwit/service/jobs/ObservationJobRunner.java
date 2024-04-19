package de.starwit.service.jobs;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.pipeline.RedisConnectionNotAvailableException;
import de.starwit.pipeline.SaeReader;
import de.starwit.service.databackend.ObservationJobService;
import de.starwit.visionapi.Messages.SaeMessage;
import jakarta.annotation.PostConstruct;

@Component
public class ObservationJobRunner implements Closeable {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${sae.redisStreamPrefix:output}")
    private String REDIS_STREAM_PREFIX;

    @Value("${sae.redisHost:localhost}")
    private String redisHost;

    @Value("${sae.redisPort:6379}")
    private int redisPort;

    private List<JobData> jobsToRun = null;
    private SaeReader saeReader = null;

    @Autowired
    private ObservationJobService observationJobService;

    @Autowired
    private AreaOccupancyJob areaOccupancyJob;

    @Autowired
    private LineCrossingJob lineCrossingJob;

    @PostConstruct
    private void init() {
        refreshJobs();
    }

    public void refreshJobs() {
        log.debug("Refreshing jobs");
        jobsToRun = new ArrayList<>();
        List<ObservationJobEntity> enabledJobs = observationJobService.findByEnabledTrue();
        for (ObservationJobEntity jobConfig : enabledJobs) {
            jobsToRun.add(new JobData(jobConfig));
        }
        refreshSaeReader();
    }
    
    private void refreshSaeReader() {
        if (saeReader != null) {
            saeReader.close();
        }

        List<String> sourceCameraIds = jobsToRun.stream()
                .map(job -> REDIS_STREAM_PREFIX + ":" + job.getConfig().getCameraId())
                .toList();

        saeReader = new SaeReader(sourceCameraIds, redisHost, redisPort);
    }

    @Scheduled(initialDelay = 1000, fixedRateString = "${analytics.jobRunInterval:10000}")
    private void runJobs() {
        if (jobsToRun != null) {
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

    @Scheduled(initialDelay = 1000, fixedRateString = "${sae.fetchDataInterval:2000}")
    private void fetchData() throws RedisConnectionNotAvailableException {
        if (jobsToRun == null) {
            return;
        }

        List<SaeMessage> messages = saeReader.read(100, 500);
        Map<String, List<SaeMessage>> messagesBySource = messages.stream().collect(Collectors.groupingBy(msg -> msg.getFrame().getSourceId()));
        for (JobData jobData : jobsToRun) {
            int discardCount = 0;
            List<SaeMessage> relevantJobMessages = messagesBySource.get(jobData.getConfig().getCameraId());
            if (relevantJobMessages == null) {
                continue;
            }
            for (SaeMessage message : relevantJobMessages) {
                for (SaeDetectionDto det : SaeDetectionDto.from(message)) {
                    boolean success = jobData.getInputData().offer(det);
                    if (!success) {
                        discardCount++;
                    }
                }
            }
            log.warn("Discarded {} messages for job {}", discardCount, jobData.getConfig().getName());
        }
    }

    @Override
    public void close() throws IOException {
        saeReader.close();
    }
}
