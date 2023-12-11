package de.starwit.service.jobs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.repository.SaeRepository;

@Component
public class LineCrossingJob extends AbstractJob<SaeDetectionEntity> {

    @Autowired
    private SaeRepository saeRepository;

    private static int MAX_WINDOW_SIZE = 3;

    private Line2D COUNTING_LINE = new Line2D.Double(450, 1010, 2240, 1640);
    private Map<String, LinkedList<Point2D>> analyzingWindowByObjId = new HashMap<>();

    @Override
    List<SaeDetectionEntity> getData(JobData<SaeDetectionEntity> jobData) {
        return saeRepository.getDetectionData(jobData.getLastRetrievedTime(), 
                jobData.getConfig().getCameraId(),
                jobData.getConfig().getDetectionClassId());
    }

    @Override
    void process(JobData<SaeDetectionEntity> jobData) {
        SaeDetectionEntity det;
        while ((det = jobData.getInputData().poll()) != null) {
            addToAnalyzingWindow(det);
            if (isWindowFilled(det)) {
                if (objectHasCrossed(det)) {
                    log.info("{} has crossed line in direction {}", det.getObjectId(), getCrossingDirection(det));
                    analyzingWindowByObjId.get(det.getObjectId()).clear();
                } else {
                    analyzingWindowByObjId.get(det.getObjectId()).removeFirst();
                }
            }
        }
    }
    
    private void addToAnalyzingWindow(SaeDetectionEntity det) {
        if (analyzingWindowByObjId.get(det.getObjectId()) == null) {
            LinkedList<Point2D> newWindow = new LinkedList<>();
            analyzingWindowByObjId.put(det.getObjectId(), newWindow);
        }
        Point2D center = centerFrom(det);
        analyzingWindowByObjId.get(det.getObjectId()).addLast(center);
    }

    private boolean isWindowFilled(SaeDetectionEntity det) {
        return analyzingWindowByObjId.get(det.getObjectId()).size() >= MAX_WINDOW_SIZE;
    }

    private boolean objectHasCrossed(SaeDetectionEntity det) {
        Point2D firstPoint = analyzingWindowByObjId.get(det.getObjectId()).getFirst();
        Point2D lastPoint = analyzingWindowByObjId.get(det.getObjectId()).getLast();
        Line2D trajectory = new Line2D.Double(firstPoint, lastPoint);
        // log.info("Check crossing for {} from {} to {}", det.getObjectId(), firstPoint, lastPoint);
        return trajectory.intersectsLine(COUNTING_LINE);
    }

    private int getCrossingDirection(SaeDetectionEntity det) {
        Point2D trajectoryEnd = analyzingWindowByObjId.get(det.getObjectId()).getLast();
        return COUNTING_LINE.relativeCCW(trajectoryEnd);
    }

    private Point2D centerFrom(SaeDetectionEntity det) {
        return new Point2D.Double(det.getMaxX() - det.getMinX(), det.getMaxY() - det.getMinY());
    }

}
