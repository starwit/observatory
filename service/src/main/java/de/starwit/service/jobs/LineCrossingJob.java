package de.starwit.service.jobs;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.repository.SaeDao;
import de.starwit.service.analytics.LineCrossingService;

@Component
public class LineCrossingJob extends AbstractJob<SaeDetectionEntity> {

    private SaeDao saeDao;

    private LineCrossingService lineCrossingService;

    private static int TARGET_WINDOW_SIZE_SEC = 1;

    private Map<Long, TrajectoryStore> trajectoryStores = new HashMap<>();
    private TrajectoryStore activeStore;
    private Line2D activeCountingLine;

    @Autowired
    public LineCrossingJob(SaeDao saeDao, LineCrossingService lineCrossingService) {
        this.saeDao = saeDao;
        this.lineCrossingService = lineCrossingService;
    }

    @Override
    List<SaeDetectionEntity> getData(JobData<SaeDetectionEntity> jobData) {
        return saeDao.getDetectionData(jobData.getLastRetrievedTime(), 
                jobData.getConfig().getCameraId(),
                jobData.getConfig().getDetectionClassId());
    }

    @Override
    void process(JobData<SaeDetectionEntity> jobData) {
        activeCountingLine = lineFrom(jobData.getConfig());
        activeStore = getStore(jobData.getConfig());
        log.debug("store size: {}", activeStore.size());

        SaeDetectionEntity det;
        while ((det = jobData.getInputData().poll()) != null) {
            activeStore.addDetection(det);
            trimTrajectory(det);
            if (isTrajectoryValid(det)) {
                if (objectHasCrossed(det)) {
                    log.info("{} has crossed line in direction {}", det.getObjectId(), getCrossingDirection(det));
                    lineCrossingService.addEntry(det, 1L, getCrossingDirection(det));
                    activeStore.clear(det);
                } else {
                    activeStore.removeFirst(det);
                }
            }
        }
        activeStore.purge(Duration.ofSeconds(5));
    }
    
    private TrajectoryStore getStore(AnalyticsJobEntity jobConfig) {
        if (trajectoryStores.get(jobConfig.getId()) == null) {
            TrajectoryStore newStore = new TrajectoryStore();
            trajectoryStores.put(jobConfig.getId(), newStore);
        }
        return trajectoryStores.get(jobConfig.getId());

    }
    
    private void trimTrajectory(SaeDetectionEntity det) {
        Instant trajectoryEnd = activeStore.getLast(det).getCaptureTs();

        boolean trimming = true;
        while (trimming) {
            Instant trajectoryStart = activeStore.getFirst(det).getCaptureTs();
            if (Duration.between(trajectoryStart, trajectoryEnd).toSeconds() > TARGET_WINDOW_SIZE_SEC) {
                activeStore.removeFirst(det);
            } else {
                trimming = false;
            }
        }

    }

    private boolean isTrajectoryValid(SaeDetectionEntity det) {
        Instant trajectoryStart = activeStore.getFirst(det).getCaptureTs();
        Instant trajectoryEnd = activeStore.getLast(det).getCaptureTs();
        return Duration.between(trajectoryStart, trajectoryEnd).toSeconds() >= TARGET_WINDOW_SIZE_SEC;
    }

    private boolean objectHasCrossed(SaeDetectionEntity det) {
        Point2D firstPoint = centerFrom(activeStore.getFirst(det));
        Point2D lastPoint = centerFrom(activeStore.getLast(det));
        Line2D trajectory = new Line2D.Double(firstPoint, lastPoint);
        return trajectory.intersectsLine(activeCountingLine);
    }


    private Direction getCrossingDirection(SaeDetectionEntity det) {
        Point2D trajectoryEnd = centerFrom(activeStore.getLast(det));
        int ccw = activeCountingLine.relativeCCW(trajectoryEnd);
        if (ccw == -1) {
            return Direction.in;
        } else {
            return Direction.out;
        }
    }

    private Point2D centerFrom(SaeDetectionEntity det) {
        return new Point2D.Double(det.getMinX() + (det.getMaxX() - det.getMinX()), det.getMinY() + (det.getMaxY() - det.getMinY()));
    }

    private Line2D lineFrom(AnalyticsJobEntity jobConfig) {
        Point2D pt1 = new Point2D.Double(jobConfig.getGeometryPoints().get(0).getX(), jobConfig.getGeometryPoints().get(0).getY());
        Point2D pt2 = new Point2D.Double(jobConfig.getGeometryPoints().get(1).getX(), jobConfig.getGeometryPoints().get(1).getY());
        return new Line2D.Double(pt1, pt2);
    }

}
