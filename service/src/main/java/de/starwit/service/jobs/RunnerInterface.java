package de.starwit.service.jobs;

import java.util.List;

import de.starwit.service.sae.SaeMessageDto;

public interface RunnerInterface {

    List<? extends JobInterface> getActiveJobs();

    FrameCounter getFrameCounter();

    default void handleMessage(SaeMessageDto dto) {
        for (JobInterface job : getActiveJobs()) {
            if (job.getConfigEntity().getStreamKey().equals(dto.getStreamKey())) {
                job.processNewMessage(dto);
                getFrameCounter().increment(job.getConfigEntity().getId());
            }
        }
    }

    void refreshJobs();

}
