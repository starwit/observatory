package de.starwit.service.jobs.areaoccupancy;

import java.time.ZonedDateTime;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;

public record AreaOccupancyObservation(ObservationJobEntity jobEntity, ZonedDateTime occupancyTime, Long count) {}