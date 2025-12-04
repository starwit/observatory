package de.starwit.service.jobs.linecrossing;

import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

public record LineCrossingObservation(SaeDetectionDto det, Direction direction, ObservationJobEntity jobEntity) {}