package de.starwit.service.jobs;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

public class LineCrossingJob implements Job {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Executor jobExecutor = Executors.newSingleThreadExecutor();

    private static int TARGET_WINDOW_SIZE_SEC = 1;

    private ObservationJobEntity configEntity;

    private TrajectoryStore trajectoryStore;
    private Line2D countingLine;
    private Boolean isGeoReferenced;
    private LineCrossingObservationListener observationListener;

    public LineCrossingJob(ObservationJobEntity configEntity, LineCrossingObservationListener observationListener) {
        this.configEntity = configEntity;
        this.observationListener = observationListener;
        this.countingLine = GeometryConverter.lineFrom(this.configEntity);
        this.isGeoReferenced = this.configEntity.getGeoReferenced();
        this.trajectoryStore = new TrajectoryStore();
    }

    @Override
    public void pushNewDetection(SaeDetectionDto dto) {
        jobExecutor.execute(() -> processNewDetection(dto));
    }

    @Override
    public ObservationJobEntity getConfigEntity() {
        return this.configEntity;
    }
    
    protected void processNewDetection(SaeDetectionDto dto) {
        log.debug("store size: {}", trajectoryStore.size());

        trajectoryStore.addDetection(dto);
        trimTrajectory(dto);
        if (isTrajectoryValid(dto)) {
            if (objectHasCrossed(dto)) {
                observationListener.onObservation(dto, getCrossingDirection(dto), this.configEntity);
                trajectoryStore.clear(dto);
            } else {
                trajectoryStore.removeFirst(dto);
            }
        }

        trajectoryStore.purge(dto.getCaptureTs());
    }
    
    private void trimTrajectory(SaeDetectionDto det) {
        Instant trajectoryEnd = trajectoryStore.getLast(det).getCaptureTs();

        boolean trimming = true;
        while (trimming) {
            Instant trajectoryStart = trajectoryStore.getFirst(det).getCaptureTs();
            if (Duration.between(trajectoryStart, trajectoryEnd).toSeconds() > TARGET_WINDOW_SIZE_SEC) {
                trajectoryStore.removeFirst(det);
            } else {
                trimming = false;
            }
        }

    }

    private boolean isTrajectoryValid(SaeDetectionDto det) {
        Instant trajectoryStart = trajectoryStore.getFirst(det).getCaptureTs();
        Instant trajectoryEnd = trajectoryStore.getLast(det).getCaptureTs();
        return Duration.between(trajectoryStart, trajectoryEnd).toSeconds() >= TARGET_WINDOW_SIZE_SEC;
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
