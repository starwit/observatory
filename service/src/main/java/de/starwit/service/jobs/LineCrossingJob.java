package de.starwit.service.jobs;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.repository.SaeDao;

@Component
public class LineCrossingJob extends AbstractJob<SaeDetectionEntity> {

    @Autowired
    private SaeDao saeDao;

    private static int TARGET_WINDOW_SIZE_SEC = 1;

    private Line2D COUNTING_LINE = new Line2D.Double(1180, 1163, 2414, 1614);
    private Map<String, LinkedList<SaeDetectionEntity>> trajectoryByObjId = new HashMap<>();

    @Override
    List<SaeDetectionEntity> getData(JobData<SaeDetectionEntity> jobData) {
        return saeDao.getDetectionData(jobData.getLastRetrievedTime(), 
                jobData.getConfig().getCameraId(),
                jobData.getConfig().getDetectionClassId());
    }

    @Override
    void process(JobData<SaeDetectionEntity> jobData) {
        SaeDetectionEntity det;
        while ((det = jobData.getInputData().poll()) != null) {
            addToTrajectory(det);
            trimTrajectory(det);
            if (isTrajectoryValid(det)) {
                if (objectHasCrossed(det)) {
                    log.info("{} has crossed line in direction {}", det.getObjectId(), getCrossingDirection(det));
                    trajectoryByObjId.get(det.getObjectId()).clear();
                } else {
                    trajectoryByObjId.get(det.getObjectId()).removeFirst();
                }
            }
        }
    }
    
    private void addToTrajectory(SaeDetectionEntity det) {
        if (trajectoryByObjId.get(det.getObjectId()) == null) {
            LinkedList<SaeDetectionEntity> newWindow = new LinkedList<>();
            trajectoryByObjId.put(det.getObjectId(), newWindow);
        }
        trajectoryByObjId.get(det.getObjectId()).addLast(det);
    }

    private void trimTrajectory(SaeDetectionEntity det) {
        Instant trajectoryEnd = trajectoryByObjId.get(det.getObjectId()).getLast().getCaptureTs();

        boolean trimming = true;
        while (trimming) {
            Instant trajectoryStart = trajectoryByObjId.get(det.getObjectId()).peekFirst().getCaptureTs();
            if (Duration.between(trajectoryStart, trajectoryEnd).toSeconds() > TARGET_WINDOW_SIZE_SEC) {
                trajectoryByObjId.get(det.getObjectId()).removeFirst();
            } else {
                trimming = false;
            }
        }

    }

    private boolean isTrajectoryValid(SaeDetectionEntity det) {
        Instant trajectoryStart = trajectoryByObjId.get(det.getObjectId()).getFirst().getCaptureTs();
        Instant trajectoryEnd = trajectoryByObjId.get(det.getObjectId()).getLast().getCaptureTs();
        return Duration.between(trajectoryStart, trajectoryEnd).toSeconds() >= TARGET_WINDOW_SIZE_SEC;
    }

    private boolean objectHasCrossed(SaeDetectionEntity det) {
        Point2D firstPoint = centerFrom(trajectoryByObjId.get(det.getObjectId()).getFirst());
        Point2D lastPoint = centerFrom(trajectoryByObjId.get(det.getObjectId()).getLast());
        Line2D trajectory = new Line2D.Double(firstPoint, lastPoint);
        return trajectory.intersectsLine(COUNTING_LINE);
    }


    private int getCrossingDirection(SaeDetectionEntity det) {
        Point2D trajectoryEnd = centerFrom(trajectoryByObjId.get(det.getObjectId()).getLast());
        return COUNTING_LINE.relativeCCW(trajectoryEnd);
    }

    private Point2D centerFrom(SaeDetectionEntity det) {
        return new Point2D.Double(det.getMinX() + (det.getMaxX() - det.getMinX()), det.getMinY() + (det.getMaxY() - det.getMinY()));
    }

}
