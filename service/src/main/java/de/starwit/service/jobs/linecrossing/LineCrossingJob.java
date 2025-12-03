package de.starwit.service.jobs.linecrossing;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.jobs.GeometryConverter;
import de.starwit.service.jobs.JobInterface;
import de.starwit.service.jobs.TrajectoryStore;
import de.starwit.service.sae.SaeDetectionDto;

public class LineCrossingJob implements JobInterface {

    protected Duration TARGET_WINDOW_SIZE;
    protected ObservationJobEntity configEntity;
    protected TrajectoryStore trajectoryStore;
    protected Line2D countingLine;
    protected Boolean isGeoReferenced;
    protected Consumer<LineCrossingObservation> observationConsumer;
    
    public LineCrossingJob(ObservationJobEntity configEntity, Duration targetWindowSize, Consumer<LineCrossingObservation> observationConsumer) {
        this.TARGET_WINDOW_SIZE = targetWindowSize;
        this.configEntity = configEntity;
        this.countingLine = GeometryConverter.lineFrom(this.configEntity);
        this.isGeoReferenced = this.configEntity.getGeoReferenced();
        this.trajectoryStore = new TrajectoryStore(targetWindowSize);
        this.observationConsumer = observationConsumer;
    }
    
    public ObservationJobEntity getConfigEntity() {
        return this.configEntity;
    }

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
    
    protected void trimTrajectory(SaeDetectionDto det) {
        Instant trajectoryEnd = trajectoryStore.getLast(det).getCaptureTs();

        boolean trimming = true;
        while (trimming) {
            Instant trajectoryStart = trajectoryStore.getFirst(det).getCaptureTs();
            if (Duration.between(trajectoryStart, trajectoryEnd).toMillis() > TARGET_WINDOW_SIZE.toMillis()) {
                trajectoryStore.removeFirst(det);
            } else {
                trimming = false;
            }
        }
    }

    protected boolean isTrajectoryValid(SaeDetectionDto det) {
        Instant trajectoryStart = trajectoryStore.getFirst(det).getCaptureTs();
        Instant trajectoryEnd = trajectoryStore.getLast(det).getCaptureTs();
        return Duration.between(trajectoryStart, trajectoryEnd).toMillis() > 0.8 * TARGET_WINDOW_SIZE.toMillis();
    }

    protected boolean objectHasCrossed(SaeDetectionDto det) {
        Point2D firstPoint = GeometryConverter.toCenterPoint(trajectoryStore.getFirst(det), isGeoReferenced);
        Point2D lastPoint = GeometryConverter.toCenterPoint(trajectoryStore.getLast(det), isGeoReferenced);
        Line2D trajectory = new Line2D.Double(firstPoint, lastPoint);
        return trajectory.intersectsLine(countingLine);
    }
    
    protected Direction getCrossingDirection(SaeDetectionDto det) {
        Point2D trajectoryEnd = GeometryConverter.toCenterPoint(trajectoryStore.getLast(det), isGeoReferenced);
        int ccw = countingLine.relativeCCW(trajectoryEnd);
        if (ccw == -1) {
            return Direction.out;
        } else {
            return Direction.in;
        }
    }

}
