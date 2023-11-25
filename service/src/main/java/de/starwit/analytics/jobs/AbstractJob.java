package de.starwit.analytics.jobs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.analytics.dtos.SaeDetectionDto;
import de.starwit.persistence.entity.output.Result;

public abstract class AbstractJob {
    final Logger log = LoggerFactory.getLogger(this.getClass());

    public void getAndProcessNewData(JobData jobData) {

        List<SaeDetectionDto> newData = this.getData(jobData);

        if (newData != null && !newData.isEmpty()) {
            jobData.setLastRetrievedTime(newData.get(newData.size() - 1).getCaptureTs());
        }

        int discardCount = 0;
        boolean success = false;
        for (int i = newData.size() - 1; i >= 0; i--) {
            success = jobData.getInputData().offer(newData.get(i));
            if (!success) {
                discardCount++;
            }
        }
        if (discardCount > 0) {
            log.warn("Discarded {} elements", discardCount);
        }

        List<? extends Result> results = this.process(jobData);

        // Pass data to database output / writer
    }

    abstract List<SaeDetectionDto> getData(JobData jobData);

    abstract List<? extends Result> process(JobData jobData);
}
