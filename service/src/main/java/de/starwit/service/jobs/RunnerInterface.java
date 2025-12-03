package de.starwit.service.jobs;

import java.time.Instant;
import java.util.List;

import de.starwit.service.sae.SaeDetectionDto;

public interface RunnerInterface {

    List<? extends JobInterface> getActiveJobs();

    default void messageHandler(SaeDetectionDto dto) {
        for (JobInterface job : getActiveJobs()) {
            if (job.getConfigEntity().getCameraId().equals(dto.getCameraId())) {
                job.processNewDetection(dto, Instant.now());
            }
        }
    }

    void refreshJobs();

}