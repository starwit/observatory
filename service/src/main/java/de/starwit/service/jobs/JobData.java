
package de.starwit.service.jobs;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import de.starwit.persistence.databackend.entity.ObservationJobEntity;

public class JobData {

    private Queue<SaeDetectionDto> inputData;
    private final ObservationJobEntity config;
    private Instant lastRetrievedTime;

    JobData(ObservationJobEntity config) {
        this.inputData = new ArrayBlockingQueue<>(2000);
        this.config = config;
        this.lastRetrievedTime = Instant.now();
    }

    public Queue<SaeDetectionDto> getInputData() {
        return inputData;
    }

    public ObservationJobEntity getConfig() {
        return config;
    }

    public Instant getLastRetrievedTime() {
        return lastRetrievedTime;
    }

    public void setLastRetrievedTime(Instant lastRetrievedTime) {
        this.lastRetrievedTime = lastRetrievedTime;
    }

}
