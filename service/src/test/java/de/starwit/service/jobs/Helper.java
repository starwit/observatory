package de.starwit.service.jobs;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.time.Instant;

import de.starwit.persistence.observatory.entity.PointEntity;
import de.starwit.service.sae.SaeDetectionDto;

public class Helper {
    
    static PointEntity createPoint(double x, double y) {
        PointEntity point = new PointEntity();
        point.setX(new BigDecimal(x));
        point.setY(new BigDecimal(y));
        return point;
    }

    static SaeDetectionDto createDetection(Instant captureTs, Point2D center, String objectId) {
        SaeDetectionDto det = new SaeDetectionDto();

        if (center != null) {
            det.setMinX(center.getX() - 50);
            det.setMaxX(center.getX() + 50);
            det.setMinY(center.getY() - 50);
            det.setMaxY(center.getY() + 50);
        }
        det.setCaptureTs(captureTs);

        // defaults (we do not need to vary these)
        det.setCameraId("camId");
        det.setClassId(1);
        det.setObjectId(objectId);
        det.setConfidence(0.5);
        
        return det;
    }
}
