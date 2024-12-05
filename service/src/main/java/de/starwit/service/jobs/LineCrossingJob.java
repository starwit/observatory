package de.starwit.service.jobs;

import java.awt.geom.Line2D;
import java.time.Duration;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;

public class LineCrossingJob {

    private ObservationJobEntity configEntity;
    private TrajectoryStore trajectoryStore;
    private Line2D countingLine;
    private Boolean isGeoReferenced;
    
    public LineCrossingJob(ObservationJobEntity configEntity, Duration targetWindowSize) {
        this.configEntity = configEntity;
        this.countingLine = GeometryConverter.lineFrom(this.configEntity);
        this.isGeoReferenced = this.configEntity.getGeoReferenced();
        this.trajectoryStore = new TrajectoryStore(targetWindowSize);
    }
    
    public ObservationJobEntity getConfigEntity() {
        return this.configEntity;
    }

    public TrajectoryStore getTrajectoryStore() {
        return trajectoryStore;
    }

    public Line2D getCountingLine() {
        return countingLine;
    }

    public Boolean isGeoReferenced() {
        return isGeoReferenced;
    }

}
