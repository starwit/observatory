package de.starwit.service.geojson;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.starwit.service.jobs.AreaOccupancyObservation;
import de.starwit.service.jobs.LineCrossingObservation;

@Service
public class GeoJsonService {
    
    @Value("${geojson.jobIncludeList:}")
    private List<String> geoJsonJobIncludeList;

    @Autowired
    private GeoJsonSenderService geoJsonService;

    public void sendLineCrossings(List<LineCrossingObservation> lineCrossingObservations) {
        // Filter observations for jobs that should be included in the GeoJSON
        List<LineCrossingObservation> filteredObservations = lineCrossingObservations.stream()
            .filter(lineCrossingObservation -> geoJsonJobIncludeList.contains(lineCrossingObservation.jobEntity().getName()))
            .toList();

        if (!filteredObservations.isEmpty()) {
            geoJsonService.sendGeoJson(GeoJsonMapper.mapLineCrossings(filteredObservations));
        }
    }

    public void sendAreaOccupancies(List<AreaOccupancyObservation> areaOccupancyObservations) {
        // Filter observations for jobs that should be included in the GeoJSON
        List<AreaOccupancyObservation> filteredObservations = areaOccupancyObservations.stream()
            .filter(areaOccupancyObservation -> geoJsonJobIncludeList.contains(areaOccupancyObservation.jobEntity().getName()))
            .toList();

        if (!filteredObservations.isEmpty()) {
            geoJsonService.sendGeoJson(GeoJsonMapper.mapAreaOccupancies(filteredObservations));
        }
    }
}
