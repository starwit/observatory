package de.starwit.service.jobs;

import java.awt.geom.Area;
import java.time.Duration;
import java.time.Instant;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;

public class AreaOccupancyJob {

    private ObservationJobEntity configEntity;
    private Area polygon;
    private Instant lastUpdate;
    private Duration analyzingInterval;
    private TrajectoryStore trajectoryStore;
    
    public AreaOccupancyJob(ObservationJobEntity configEntity, Duration analyzingInterval) {
        this.configEntity = configEntity;
        this.polygon = GeometryConverter.areaFrom(configEntity);
        this.lastUpdate = Instant.ofEpochMilli(0);
        this.analyzingInterval = analyzingInterval;
        this.trajectoryStore = new TrajectoryStore(this.analyzingInterval);
    }
    
    public ObservationJobEntity getConfigEntity() {
        return this.configEntity;
    }
    
    public Area getPolygon() {
        return polygon;
    }
    
    public Instant getLastUpdate() {
        return lastUpdate;
    }
    
    public Instant setLastUpdate(Instant lastUpdate) {
        return lastUpdate;
    }

    public Duration getAnalyzingInterval() {
        return analyzingInterval;
    }

    public TrajectoryStore getTrajectoryStore() {
        return trajectoryStore;
    }
}
