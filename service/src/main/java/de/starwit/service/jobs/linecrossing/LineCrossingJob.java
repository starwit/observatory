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

    private Duration TARGET_WINDOW_SIZE;
    private ObservationJobEntity configEntity;
    private TrajectoryStore trajectoryStore;
    private Line2D countingLine;
    private Boolean isGeoReferenced;
    private Consumer<LineCrossingObservation> observationConsumer;
    
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

    public void processNewDetection(SaeDetectionDto dto) {
        trajectoryStore.addDetection(dto);
        trajectoryStore.trimTrajectory(dto);

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
        return trajectoryStore.trajectoryLength(det).toMillis() > 0.8 * TARGET_WINDOW_SIZE.toMillis();
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
