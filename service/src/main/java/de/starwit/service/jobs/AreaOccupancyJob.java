package de.starwit.service.jobs;

import java.awt.geom.Area;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.service.analytics.AreaOccupancyService;

@Component
public class AreaOccupancyJob extends AbstractJob {

    private AreaOccupancyService areaOccupancyService;

    private Boolean isGeoReferenced;
    
    @Autowired
    public AreaOccupancyJob(AreaOccupancyService areaOccupancyService) {
        this.areaOccupancyService = areaOccupancyService;
    }

    @Override
    List<SaeDetectionDto> getData(JobData jobData) {
        return new ArrayList<>();
    }

    @Override
    void process(JobData jobData) throws InterruptedException {
        if (jobData != null) {
            isGeoReferenced = jobData.getConfig().getGeoReferenced();
            
            Queue<SaeDetectionDto> queue = jobData.getInputData();
            if (queue == null) {
                return;
            }

            Map<Long, List<SaeDetectionDto>> detByCaptureTs = queue.stream().collect(Collectors.groupingBy(det -> det.getCaptureTs().toEpochMilli()));

            Area polygon = GeometryConverter.areaFrom(jobData.getConfig());

            long maxCount = 0L;
            ZonedDateTime maxTs = ZonedDateTime.now();
            for (List<SaeDetectionDto> detList : detByCaptureTs.values()) {
                long count = objCountInPolygon(detList, polygon);
                if (count > maxCount) {
                    maxCount = count;
                    maxTs = detList.get(0).getCaptureTs().atZone(ZoneOffset.UTC);
                }
            }

            areaOccupancyService.addEntry(jobData.getConfig(), maxTs, maxCount);
        }
    }

    private Long objCountInPolygon(List<SaeDetectionDto> objects, Area polygon) {
        return objects.stream()
            .filter(det -> polygon.contains(GeometryConverter.toCenterPoint(det, isGeoReferenced)))
            .count();
    }

}
