package de.starwit.service.jobs;

import java.awt.geom.Area;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.repository.SaeDao;
import de.starwit.service.analytics.AreaOccupancyService;

@Component
public class AreaOccupancyJob extends AbstractJob<SaeDetectionEntity> {

    private AreaOccupancyService areaOccupancyService;

    private SaeDao saeDao;

    private Boolean isGeoReferenced;
    
    @Autowired
    public AreaOccupancyJob(SaeDao saeDao, AreaOccupancyService areaOccupancyService) {
        this.areaOccupancyService = areaOccupancyService;
        this.saeDao = saeDao;
    }

    @Override
    List<SaeDetectionEntity> getData(JobData<SaeDetectionEntity> jobData) {
        return saeDao.getDetectionData(jobData.getLastRetrievedTime(),
                jobData.getConfig().getCameraId(),
                jobData.getConfig().getDetectionClassId());
    }

    @Override
    void process(JobData<SaeDetectionEntity> jobData) throws InterruptedException {
        if (jobData != null) {
            isGeoReferenced = jobData.getConfig().getGeoReferenced();
            
            Queue<SaeDetectionEntity> queue = jobData.getInputData();
            if (queue == null) {
                return;
            }

            Map<Long, List<SaeDetectionEntity>> detByCaptureTs = queue.stream().collect(Collectors.groupingBy(det -> det.getCaptureTs().toEpochMilli()));

            Area polygon = GeometryConverter.areaFrom(jobData.getConfig());

            long maxCount = 0L;
            ZonedDateTime maxTs = ZonedDateTime.now();
            for (List<SaeDetectionEntity> detList : detByCaptureTs.values()) {
                long count = objCountInPolygon(detList, polygon);
                if (count > maxCount) {
                    maxCount = count;
                    maxTs = detList.get(0).getCaptureTs().atZone(ZoneOffset.UTC);
                }
            }

            areaOccupancyService.addEntry(jobData.getConfig(), maxTs, maxCount);
        }
    }

    private Long objCountInPolygon(List<SaeDetectionEntity> objects, Area polygon) {
        return objects.stream()
            .filter(det -> polygon.contains(GeometryConverter.toCenterPoint(det, isGeoReferenced)))
            .count();
    }

}
