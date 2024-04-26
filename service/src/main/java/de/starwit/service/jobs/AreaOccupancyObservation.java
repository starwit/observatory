package de.starwit.service.jobs;

import java.time.ZonedDateTime;

import de.starwit.persistence.databackend.entity.ObservationJobEntity;

public record AreaOccupancyObservation(ObservationJobEntity jobEntity, ZonedDateTime occupancyTime, Long count) {}