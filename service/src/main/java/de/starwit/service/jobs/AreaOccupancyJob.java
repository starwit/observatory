package de.starwit.service.jobs;

import java.awt.geom.Area;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

public class AreaOccupancyJob extends AbstractJob {

    private AreaOccupancyObservationListener observationListener;

    private static Duration SLIDING_WINDOW_TARGET_LENGTH = Duration.ofSeconds(5);
    private LinkedList<SaeDetectionDto> slidingWindow = new LinkedList<>();

    public AreaOccupancyJob(ObservationJobEntity configEntity, AreaOccupancyObservationListener observationListener) {
        super(configEntity);
        this.observationListener = observationListener;
    }

    @Override
    protected void processNewDetection(SaeDetectionDto dto) {
        addDataToSlidingWindow(dto);
        if (slidingWindowLength().toMillis() < SLIDING_WINDOW_TARGET_LENGTH.toMillis()) {
            return;
        }

        Map<Long, List<SaeDetectionDto>> detByCaptureTs = this.slidingWindow.stream().collect(Collectors.groupingBy(det -> det.getCaptureTs().toEpochMilli()));
    
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
        slidingWindow.clear();
    }

    private void addDataToSlidingWindow(SaeDetectionDto dto) {
        slidingWindow.addLast(dto);

        // Keep sliding window within target time interval
        Instant windowEndTime = dto.getCaptureTs();
        Instant windowStartTime = slidingWindow.peekFirst().getCaptureTs();
        while (Duration.between(windowStartTime, windowEndTime).minus(SLIDING_WINDOW_TARGET_LENGTH).toMillis() > 0) {
            slidingWindow.removeFirst();
            windowStartTime = slidingWindow.peekFirst().getCaptureTs();
        }
    }

    private Duration slidingWindowLength() {
        return Duration.between(slidingWindow.peekFirst().getCaptureTs(), slidingWindow.peekLast().getCaptureTs());
    }

    private Long objCountInPolygon(List<SaeDetectionDto> objects, Area polygon) {
        return objects.stream()
            .filter(det -> polygon.contains(GeometryConverter.toCenterPoint(det, configEntity.getGeoReferenced())))
            .count();
    }

}
