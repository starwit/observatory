package de.starwit.service.jobs;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractJob {

    @Value("${analytics.maxDataInterval:10000}")
    int maxDataInterval;
    
    final Logger log = LoggerFactory.getLogger(this.getClass());

    public void run(JobData jobData) throws InterruptedException {

        if (jobData.getLastRetrievedTime().isBefore(Instant.now().minusMillis(maxDataInterval))) {
            jobData.setLastRetrievedTime(Instant.now().minusMillis(maxDataInterval));
        }

        List<SaeDetectionDto> newData = this.getData(jobData);

        int discardCount = 0;
        boolean success = false;

        if (newData != null && !newData.isEmpty()) {
            jobData.setLastRetrievedTime(newData.get(newData.size() - 1).getCaptureTs());

            for (SaeDetectionDto dataPoint : newData) {
                success = jobData.getInputData().offer(dataPoint);
                if (!success) {
                    discardCount++;
                }
            }
        }
        
        if (discardCount > 0) {
            log.warn("Discarded {} elements", discardCount);
        }

        this.process(jobData);
    }

    abstract List<SaeDetectionDto> getData(JobData jobData);

    abstract void process(JobData jobData) throws InterruptedException;
}
