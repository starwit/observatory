package de.starwit.service.analytics;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import de.starwit.persistence.analytics.entity.ObjectClassEntity;
import de.starwit.persistence.analytics.repository.AreaOccupancyRepository;
import de.starwit.persistence.analytics.repository.ObjectClassRepository;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.service.impl.ServiceInterface;

/**
 * 
 * AreaOccupancy Service class
 *
 */
@Service
public class AreaOccupancyService implements ServiceInterface<AreaOccupancyEntity, AreaOccupancyRepository> {

    @Autowired
    private AreaOccupancyRepository areaoccupancyRepository;

    @Autowired
    private ObjectClassRepository objectClassRepository;

    @Override
    public AreaOccupancyRepository getRepository() {
        return areaoccupancyRepository;
    }

    /**
     * Save Area occupancies for a minute.
     * 
     * @param saeEntry
     */
    public void addEntry(SaeDetectionEntity saeEntry) {
        AreaOccupancyEntity entity = new AreaOccupancyEntity();
        ZonedDateTime dataTime = ZonedDateTime.ofInstant(saeEntry.getCaptureTs(), ZoneId.systemDefault());
        ZonedDateTime timeUnit = dataTime.truncatedTo(ChronoUnit.MINUTES);
        entity.setOccupancyTime(timeUnit);
        entity.setCount(1);

        ObjectClassEntity objectClassEntity = new ObjectClassEntity();
        List<ObjectClassEntity> objectClassList = objectClassRepository.findByClassId(saeEntry.getClassId());
        if (objectClassList != null && !objectClassList.isEmpty()) {
            entity.setObjectClass(objectClassList.get(0));
            List<AreaOccupancyEntity> entityList = areaoccupancyRepository.findByOccupancyTimeAndObjectClass(timeUnit,
                    saeEntry.getClassId());
            if (entityList != null && !entityList.isEmpty()) {
                entity = entityList.get(0);
                entity.setCount(entity.getCount() + 1);
            }
        } else {
            objectClassEntity.setClassId(saeEntry.getClassId());
            objectClassEntity.setName("undefined");
            objectClassEntity = objectClassRepository.saveAndFlush(objectClassEntity);
            entity.setObjectClass(objectClassEntity);
        }
        areaoccupancyRepository.saveAndFlush(entity);
    }

}
