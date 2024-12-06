package de.starwit.service.jobs;

import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.persistence.observatory.entity.PointEntity;
import de.starwit.service.sae.SaeDetectionDto;

/**
 * This class is a workaround until we have a proper concept 
 * for the two modes "geo-referenced" and "pixel-based".
 * From the point of view of the algorithms in this project,
 * both cases use coordinates into a 2-dimensional Euclidean space,
 * just the value ranges are different.
 * The intention is to encode the knowledge on how to handle,
 * i.e. read geo-referenced data in one place, 
 * s.t. it'll be easier to migrate to a proper solution.
 */
public class GeometryConverter {

    /**
     * Derive a java.awt.geo.Area from the geometry in an ObservationJob.
     * Reads appropriate coordinates depending on whether geoReferenced is true.
     * @param jobConfig
     */
    public static Area areaFrom(ObservationJobEntity jobConfig) {
        Path2D.Double path = new Path2D.Double();
        Point2D firstPoint = fromPoint(jobConfig.getGeometryPoints().get(0), jobConfig.getGeoReferenced());
        path.moveTo(firstPoint.getX(), firstPoint.getY());

        for (int i = 1;i < jobConfig.getGeometryPoints().size(); i++) {
            Point2D point = fromPoint(jobConfig.getGeometryPoints().get(i), jobConfig.getGeoReferenced());
            path.lineTo(point.getX(), point.getY());
        }
        
        return new Area(path);
    }

    /**
     * Derive a java.awt.geom.Line2D from an ObservationJob.
     * Reads appropriate coordinates depending on whether geoReferenced is true.
     * @param jobConfig
     */
    public static Line2D lineFrom(ObservationJobEntity jobConfig) {
        Point2D pt1, pt2;
        
        pt1 = fromPoint(jobConfig.getGeometryPoints().get(0), jobConfig.getGeoReferenced());
        pt2 = fromPoint(jobConfig.getGeometryPoints().get(1), jobConfig.getGeoReferenced());

        return new Line2D.Double(pt1, pt2);
    }

    public static List<Point2D> toCenterPoints(List<SaeDetectionDto> detections, boolean isGeo) {
        return detections.stream().map(det -> toCenterPoint(det, isGeo)).toList();
    }

    public static Point2D toCenterPoint(SaeDetectionDto detection, boolean isGeo) {
        double centerX, centerY;
        if (isGeo) {
            centerX = detection.getLongitude();
            // Geo coordinate system has y-axis flipped upside down (compared to image coordinate system)
            // therefore we need to reverse the latitude sign to make everything consistent.
            centerY = -detection.getLatitude();
        } else {
            centerX = (detection.getMaxX() + detection.getMinX()) / 2;
            centerY = (detection.getMaxY() + detection.getMinY()) / 2;
        }
        return new Point2D.Double(centerX, centerY);
    }

    public static Point2D fromPoint(PointEntity entity, boolean isGeo) {
        if (isGeo) {
            // See comment in toCenterPoint()
            return new Point2D.Double(entity.getLongitude().doubleValue(), -entity.getLatitude().doubleValue());
        } else {
            return new Point2D.Double(entity.getX().doubleValue(), entity.getY().doubleValue());
        }
    }
}
