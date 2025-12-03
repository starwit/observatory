package de.starwit.service.jobs;

import java.time.Instant;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

public interface JobInterface {

    ObservationJobEntity getConfigEntity();

    // `run()` and `addDetection()` are called from different threads, so we need to lock to make sure data is consistent.
    // If this becomes a performance bottleneck, we could optimize this away, e.g. by using a queue for input data and updating the `TrajectoryStore` from that queue during `run()`
    void processNewDetection(SaeDetectionDto dto, Instant currentTime);

}