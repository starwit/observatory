package de.starwit.service.jobs;

import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;
import de.starwit.persistence.databackend.entity.PointEntity;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;

public class GeometryConverter {
    public static Area areaFrom(AnalyticsJobEntity jobConfig) {
        Path2D.Double path = new Path2D.Double();
        Point2D firstPoint = fromPoint(jobConfig.getGeometryPoints().get(0), jobConfig.getGeoReferenced());
        path.moveTo(firstPoint.getX(), firstPoint.getY());

        for (int i = 1;i < jobConfig.getGeometryPoints().size(); i++) {
            Point2D point = fromPoint(jobConfig.getGeometryPoints().get(i), jobConfig.getGeoReferenced());
            path.lineTo(point.getX(), point.getY());
        }
        
        return new Area(path);
    }

    public static Line2D lineFrom(AnalyticsJobEntity jobConfig) {
        Point2D pt1, pt2;
        
        pt1 = fromPoint(jobConfig.getGeometryPoints().get(0), jobConfig.getGeoReferenced());
        pt2 = fromPoint(jobConfig.getGeometryPoints().get(1), jobConfig.getGeoReferenced());

        return new Line2D.Double(pt1, pt2);
    }

    public static Point2D toCenterPoint(SaeDetectionEntity detection, boolean isGeo) {
        double centerX, centerY;
        if (isGeo) {
            centerX = detection.getLongitude();
            centerY = detection.getLatitude();
        } else {
            centerX = (detection.getMaxX() + detection.getMinX()) / 2;
            centerY = (detection.getMaxY() + detection.getMinY()) / 2;
        }
        return new Point2D.Double(centerX, centerY);
    }

    public static Point2D fromPoint(PointEntity entity, boolean isGeo) {
        if (isGeo) {
            return new Point2D.Double(entity.getLongitude().doubleValue(), entity.getLatitude().doubleValue());
        } else {
            return new Point2D.Double(entity.getX().doubleValue(), entity.getY().doubleValue());
        }
    }
}
