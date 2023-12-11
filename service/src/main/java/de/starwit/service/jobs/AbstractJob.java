package de.starwit.service.jobs;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import de.starwit.persistence.common.entity.AbstractCaptureEntity;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;

public abstract class AbstractJob<E extends AbstractCaptureEntity> {

    @Value("${analytics.maxDataInterval:10000}")
    int maxDataInterval;
    
    final Logger log = LoggerFactory.getLogger(this.getClass());

    public void run(JobData<E> jobData) throws InterruptedException {

        if (jobData.getLastRetrievedTime().isBefore(Instant.now().minusMillis(maxDataInterval))) {
            jobData.setLastRetrievedTime(Instant.now().minusMillis(maxDataInterval));
        }

        List<E> newData = this.getData(jobData);

        int discardCount = 0;
        boolean success = false;

        if (newData != null && !newData.isEmpty()) {
            jobData.setLastRetrievedTime(newData.get(newData.size() - 1).getCaptureTs());

            for (E dataPoint : newData) {
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

    abstract List<E> getData(JobData<E> jobData);

    abstract void process(JobData<E> jobData) throws InterruptedException;
}
