package de.starwit.service.jobs;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;

import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;
import de.starwit.persistence.databackend.entity.JobType;
import de.starwit.persistence.databackend.entity.PointEntity;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;

public class Helper {
    
    static PointEntity createPoint(double x, double y) {
        PointEntity point = new PointEntity();
        point.setX(new BigDecimal(x));
        point.setY(new BigDecimal(y));
        return point;
    }

    static SaeDetectionEntity createDetection(Instant captureTs, Point2D center) {
        SaeDetectionEntity det = new SaeDetectionEntity();

        det.setCaptureTs(captureTs);
        det.setMinX(center.getX() - 50);
        det.setMaxX(center.getX() + 50);
        det.setMinY(center.getY() - 50);
        det.setMaxY(center.getY() + 50);

        // defaults (we do not need to vary these)
        det.setCameraId("camId");
        det.setClassId(1);
        det.setObjectId("objId");
        det.setConfidence(0.5);
        
        return det;
    }
}
