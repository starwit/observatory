package de.starwit.service.jobs.flow;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.jobs.linecrossing.LineCrossingJob;
import de.starwit.service.jobs.linecrossing.LineCrossingObservation;
import de.starwit.service.sae.SaeDetectionDto;

public class FlowJob extends LineCrossingJob {
    public FlowJob(ObservationJobEntity configEntity, Duration targetWindowSize, Consumer<LineCrossingObservation> observationConsumer) {
        super(configEntity, targetWindowSize, observationConsumer);
    }

    @Override
    public void processNewDetection(SaeDetectionDto dto, Instant currentTime) {
        trajectoryStore.addDetection(dto);
        trimTrajectory(dto);
        if (isTrajectoryValid(dto)) {
            if (objectHasCrossed(dto)) {
                observationConsumer.accept(new LineCrossingObservation(dto, getCrossingDirection(dto), configEntity));
                trajectoryStore.clear(dto);
            }
        }

        trajectoryStore.purge(dto.getCaptureTs());
    }

}
