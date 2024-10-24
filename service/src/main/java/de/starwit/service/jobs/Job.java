package de.starwit.service.jobs;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

public interface Job {

    /**
     * Feed a new detection into the Job.
     * This is safe to be called on a message handler thread (jobs must handle their processing in a separate thread!)
     * @param dto
     */
    public void pushNewDetection(SaeDetectionDto dto);

    public ObservationJobEntity getConfigEntity();

    public void stop();

}
