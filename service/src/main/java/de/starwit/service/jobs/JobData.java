
package de.starwit.service.jobs;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import de.starwit.persistence.common.entity.AbstractCaptureEntity;
import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;

public class JobData<E extends AbstractCaptureEntity> {

    private Queue<E> inputData;
    private final AnalyticsJobEntity config;
    private Instant lastRetrievedTime;

    JobData(AnalyticsJobEntity config) {
        this.inputData = new ArrayBlockingQueue<>(500);
        this.config = config;
        this.lastRetrievedTime = Instant.now();
    }

    public Queue<E> getInputData() {
        return inputData;
    }

    public AnalyticsJobEntity getConfig() {
        return config;
    }

    public Instant getLastRetrievedTime() {
        return lastRetrievedTime;
    }

    public void setLastRetrievedTime(Instant lastRetrievedTime) {
        this.lastRetrievedTime = lastRetrievedTime;
    }

}
