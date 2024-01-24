package de.starwit.service.jobs;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.databackend.entity.PointEntity;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.repository.SaeDao;
import de.starwit.service.analytics.AreaOccupancyService;

@Component
public class AreaOccupancyJob extends AbstractJob<SaeDetectionEntity> {

    private AreaOccupancyService areaOccupancyService;

    private SaeDao saeDao;
    
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
            Queue<SaeDetectionEntity> queue = jobData.getInputData();
            if (queue == null) {
                return;
            }

            Map<Long, List<SaeDetectionEntity>> detByCaptureTs = queue.stream().collect(Collectors.groupingBy(det -> det.getCaptureTs().toEpochMilli()));

            Area polygon = toArea(jobData.getConfig().getGeometryPoints());

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

    private Area toArea(List<PointEntity> polygonPoints) {
        Path2D.Double path = new Path2D.Double();
        PointEntity firstPoint = polygonPoints.get(0);
        path.moveTo(firstPoint.getX().doubleValue(), firstPoint.getY().doubleValue());

        for (int i = 1;i < polygonPoints.size(); i++) {
            PointEntity point = polygonPoints.get(i);
            path.lineTo(point.getX().doubleValue(), point.getY().doubleValue());
        }
        
        return new Area(path);
    }

    private Long objCountInPolygon(List<SaeDetectionEntity> objects, Area polygon) {
        return objects.stream()
            .filter(det -> polygon.contains(toCenterPoint(det)))
            .count();
    }

    private Point2D toCenterPoint(SaeDetectionEntity detection) {
        double centerX = (detection.getMaxX() + detection.getMinX()) / 2;
        double centerY = (detection.getMaxY() + detection.getMinY()) / 2;
        return new Point2D.Double(centerX, centerY);
    }
}
