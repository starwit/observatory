package de.starwit.service.geojson;

import java.time.ZoneOffset;
import java.util.List;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;

import de.starwit.persistence.databackend.entity.PointEntity;
import de.starwit.service.jobs.AreaOccupancyObservation;
import de.starwit.service.jobs.LineCrossingObservation;

public class GeoJsonMapper {

    public static GeoJsonObject mapLineCrossings(List<LineCrossingObservation> lineCrossingObservations) {
        FeatureCollection featureCollection = new FeatureCollection();

        List<Feature> features = lineCrossingObservations.stream()
            .map(obs -> (Feature) mapLineCrossing(obs))
            .toList();

        featureCollection.setFeatures(features);

        return featureCollection;
    }

    public static GeoJsonObject mapAreaOccupancies(List<AreaOccupancyObservation> areaOccupancyObservations) {
        FeatureCollection featureCollection = new FeatureCollection();

        List<Feature> features = areaOccupancyObservations.stream() 
            .map(obs -> (Feature) mapAreaOccupancy(obs))
            .toList();

        featureCollection.setFeatures(features);

        return featureCollection;
    }

    public static GeoJsonObject mapLineCrossing(LineCrossingObservation lineCrossingObservation) {
        Feature feature = new Feature();
        
        List<PointEntity> points = lineCrossingObservation.jobEntity().getGeometryPoints();

        feature.setGeometry(new LineString(
            toLngLatAlt(points.get(0)),
            toLngLatAlt(points.get(1))
        ));

        feature.setProperty("timestamp", lineCrossingObservation.det().getCaptureTs().atZone(ZoneOffset.UTC).toString());
        feature.setProperty("object_class_id", lineCrossingObservation.det().getClassId());
        feature.setProperty("direction", lineCrossingObservation.direction().name());
        feature.setProperty("name", lineCrossingObservation.jobEntity().getName());

        return feature;
    }

    public static GeoJsonObject mapAreaOccupancy(AreaOccupancyObservation areaOccupancyObservation) {
        Feature feature = new Feature();

        List<PointEntity> points = areaOccupancyObservation.jobEntity().getGeometryPoints();
        
        List<LngLatAlt> lngLatAltPoints = points.stream().map(GeoJsonMapper::toLngLatAlt).toList();
        
        // Add first point again as per GeoJson spec (closing polygon)
        lngLatAltPoints.add(lngLatAltPoints.get(0));

        feature.setGeometry(new Polygon(lngLatAltPoints));
        
        feature.setProperty("timestamp", areaOccupancyObservation.occupancyTime().toString());
        feature.setProperty("name", areaOccupancyObservation.jobEntity().getName());
        feature.setProperty("count", areaOccupancyObservation.count());
        
        return feature;
    }

    private static LngLatAlt toLngLatAlt(PointEntity pointEntity) {
        LngLatAlt lngLatAlt = new LngLatAlt();
        lngLatAlt.setLongitude(pointEntity.getLongitude().doubleValue());
        lngLatAlt.setLatitude(pointEntity.getLatitude().doubleValue());
        return lngLatAlt;
    }
}
