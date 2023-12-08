package de.starwit.service.jobs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.persistence.common.entity.AbstractCaptureEntity;

public abstract class AbstractJob<E extends AbstractCaptureEntity> {
    final Logger log = LoggerFactory.getLogger(this.getClass());

    public void getAndProcessNewData(JobData<E> jobData) throws InterruptedException {

        List<E> newData = this.getData(jobData);

        int discardCount = 0;
        boolean success = false;

        if (newData != null && !newData.isEmpty()) {
            jobData.setLastRetrievedTime(newData.get(newData.size() - 1).getCaptureTs());

            for (int i = newData.size() - 1; i >= 0; i--) {
                success = jobData.getInputData().offer(newData.get(i));
                if (!success) {
                    discardCount++;
                }
            }
        }
        if (discardCount > 0) {
            log.warn("Discarded {} elements", discardCount);
        }

        this.process(jobData);

        // Pass data to database output / writer
    }

    abstract List<E> getData(JobData<E> jobData);

    abstract void process(JobData<E> jobData) throws InterruptedException;
}
