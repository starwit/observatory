package de.starwit.service.jobs;

import java.util.List;

import de.starwit.service.sae.SaeMessageDto;

public interface RunnerInterface {

    List<? extends JobInterface> getActiveJobs();

    default void messageHandler(SaeMessageDto dto) {
        for (JobInterface job : getActiveJobs()) {
            if (job.getConfigEntity().getCameraId().equals(dto.getCameraId())) {
                job.processNewMessage(dto);
            }
        }
    }

    void refreshJobs();

}