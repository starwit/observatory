package de.starwit.service.jobs;

import java.awt.geom.Area;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

public class AreaOccupancyJob extends AbstractJob {

    private AreaOccupancyObservationListener observationListener;

    private static Duration ANALYZING_WINDOW_LENGTH = Duration.ofSeconds(5);
    private LinkedList<SaeDetectionDto> detectionBuffer = new LinkedList<>();

    public AreaOccupancyJob(ObservationJobEntity configEntity, AreaOccupancyObservationListener observationListener) {
        super(configEntity);
        this.observationListener = observationListener;
    }

    @Override
    protected void processNewDetection(SaeDetectionDto dto) {
        detectionBuffer.add(dto);
        if (!isBufferHealthy()) {
            return;
        }

        Map<Long, List<SaeDetectionDto>> detByCaptureTs = this.detectionBuffer.stream().collect(Collectors.groupingBy(det -> det.getCaptureTs().toEpochMilli()));
    
        Area polygon = GeometryConverter.areaFrom(this.configEntity);
    
        long maxCount = 0L;
        ZonedDateTime maxTs = ZonedDateTime.now();
        for (List<SaeDetectionDto> detList : detByCaptureTs.values()) {
            long count = objCountInPolygon(detList, polygon);
            if (count > maxCount) {
                maxCount = count;
                maxTs = detList.get(0).getCaptureTs().atZone(ZoneOffset.UTC);
            }
        }
    
        observationListener.onObservation(this.configEntity, maxTs, maxCount);
        detectionBuffer.clear();
    }

    private boolean isBufferHealthy() {
        return bufferLength().toMillis() >= ANALYZING_WINDOW_LENGTH.toMillis();
    }

    private Duration bufferLength() {
        return Duration.between(detectionBuffer.peekFirst().getCaptureTs(), detectionBuffer.peekLast().getCaptureTs());
    }

    private Long objCountInPolygon(List<SaeDetectionDto> objects, Area polygon) {
        return objects.stream()
            .filter(det -> polygon.contains(GeometryConverter.toCenterPoint(det, configEntity.getGeoReferenced())))
            .count();
    }

}
