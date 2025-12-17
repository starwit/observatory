package de.starwit.service.jobs.linecrossing;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.util.function.Consumer;

import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.jobs.GeometryConverter;
import de.starwit.service.jobs.JobInterface;
import de.starwit.service.jobs.TrajectoryStore;
import de.starwit.service.sae.SaeDetectionDto;

public class LineCrossingJob implements JobInterface {

    private final ObservationJobEntity configEntity;
    private final Duration TARGET_WINDOW_SIZE;
    private final Consumer<LineCrossingObservation> observationConsumer;
    
    private final Line2D countingLine;
    private final Boolean isGeoReferenced;
    private final TrajectoryStore trajectoryStore;
    
    public LineCrossingJob(ObservationJobEntity configEntity, Duration targetWindowSize, Consumer<LineCrossingObservation> observationConsumer) {
        this.configEntity = configEntity;
        this.TARGET_WINDOW_SIZE = targetWindowSize;
        this.observationConsumer = observationConsumer;
        
        this.countingLine = GeometryConverter.lineFrom(this.configEntity);
        this.isGeoReferenced = this.configEntity.getGeoReferenced();
        this.trajectoryStore = new TrajectoryStore();
    }
    
    public ObservationJobEntity getConfigEntity() {
        return this.configEntity;
    }

    public void processNewDetection(SaeDetectionDto dto) {
        trajectoryStore.addDetection(dto);
        trajectoryStore.trimSingleRelative(dto, TARGET_WINDOW_SIZE);

        if (isTrajectoryLongEnough(dto)) {
            if (objectHasCrossed(dto)) {
                observationConsumer.accept(new LineCrossingObservation(dto, getCrossingDirection(dto), configEntity));
                trajectoryStore.clear(dto);
            }
        }

        // TODO Make this configurable
        trajectoryStore.purge(dto.getCaptureTs().minus(Duration.ofMinutes(1)));
    }

    private boolean isTrajectoryLongEnough(SaeDetectionDto det) {
        return trajectoryStore.length(det).toMillis() > 0.8 * TARGET_WINDOW_SIZE.toMillis();
    }

    private boolean objectHasCrossed(SaeDetectionDto det) {
        Point2D firstPoint = GeometryConverter.toCenterPoint(trajectoryStore.getFirst(det), isGeoReferenced);
        Point2D lastPoint = GeometryConverter.toCenterPoint(trajectoryStore.getLast(det), isGeoReferenced);
        Line2D trajectory = new Line2D.Double(firstPoint, lastPoint);
        return trajectory.intersectsLine(countingLine);
    }
    
    private Direction getCrossingDirection(SaeDetectionDto det) {
        Point2D trajectoryEnd = GeometryConverter.toCenterPoint(trajectoryStore.getLast(det), isGeoReferenced);
        int ccw = countingLine.relativeCCW(trajectoryEnd);
        if (ccw == -1) {
            return Direction.out;
        } else {
            return Direction.in;
        }
    }

}
