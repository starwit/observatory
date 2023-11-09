package de.starwit.service.analytics;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.persistence.entity.AnalyticsJobEntity;
import de.starwit.persistence.entity.input.SaeInput;
import de.starwit.persistence.entity.output.Result;

public class Job {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ArrayBlockingQueue<SaeInput> inputData;
    private final Algorithm algorithm;
    private final AnalyticsJobEntity config;

    Job(Algorithm algorithm, AnalyticsJobEntity config) {
        this.algorithm = algorithm;
        this.inputData = new ArrayBlockingQueue<>(100);
        this.config = config;
    }

    public void feed(List<SaeInput> newData) {
        int discardCount = 0;
        boolean success = false;
        for (int i = newData.size() - 1; i >= 0; i--) {
            success = this.inputData.offer(newData.get(i));
            if (!success) {
                discardCount++;
            }
        }
        if (discardCount > 0) {
            log.warn("Discarded {} elements", discardCount);
        }

        List<? extends Result> results = this.algorithm.process(this.inputData);
        
        // Pass data to database output / writer
    }

    public AnalyticsJobEntity getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return String.format(
                "Job(algorithm=%s, name=%s, parkingareaid=%s)",
                this.algorithm.getClass().getSimpleName(),
                this.config.getName(),
                this.config.getParkingAreaId());
    }

    public static Job from(AnalyticsJobEntity jobConfig) {
        switch (jobConfig.getType()) {
            case LINE_CROSSING:
                return new Job(new LineCrossingAlgorithm(), jobConfig);

            case AREA_OCCUPANCY:
                return new Job(new AreaOccpancyAlgorithm(), jobConfig);

            default:
                return null;
        }
    }

}
