
package de.starwit.service.jobs;

import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;

import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;

public class JobData {

    private ArrayBlockingQueue<SaeDetectionEntity> inputData;
    private final AnalyticsJobEntity config;
    private Instant lastRetrievedTime;

    JobData(AnalyticsJobEntity config) {
        this.inputData = new ArrayBlockingQueue<>(100);
        this.config = config;
        this.lastRetrievedTime = Instant.now();
    }

    public ArrayBlockingQueue<SaeDetectionEntity> getInputData() {
        return inputData;
    }

    public void setInputData(ArrayBlockingQueue<SaeDetectionEntity> inputData) {
        this.inputData = inputData;
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
