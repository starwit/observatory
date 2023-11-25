
package de.starwit.analytics.jobs;

import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;

import de.starwit.analytics.dtos.SaeDetectionDto;
import de.starwit.persistence.entity.AnalyticsJobEntity;

public class JobData {

    private ArrayBlockingQueue<SaeDetectionDto> inputData;
    private final AnalyticsJobEntity config;
    private Instant lastRetrievedTime;

    JobData(AnalyticsJobEntity config) {
        this.inputData = new ArrayBlockingQueue<>(100);
        this.config = config;
        this.lastRetrievedTime = Instant.now();
    }

    public ArrayBlockingQueue<SaeDetectionDto> getInputData() {
        return inputData;
    }

    public void setInputData(ArrayBlockingQueue<SaeDetectionDto> inputData) {
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
