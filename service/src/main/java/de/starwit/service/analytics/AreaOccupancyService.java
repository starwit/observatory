package de.starwit.service.analytics;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import de.starwit.persistence.analytics.entity.ObjectClassEntity;
import de.starwit.persistence.analytics.repository.AreaOccupancyRepository;
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

    @Override
    public AreaOccupancyRepository getRepository() {
        return areaoccupancyRepository;
    }

    public void addEntry(SaeDetectionEntity saeEntry) {
        AreaOccupancyEntity entity = new AreaOccupancyEntity();
        entity.setOccupancyTime(ZonedDateTime.now());
        entity.setCount(4);
        ObjectClassEntity objectClassEntity = new ObjectClassEntity();
        objectClassEntity.setClassId(2);
        objectClassEntity.setName("car");
        // objectClassEntity = objectClassRepository.save(objectClassEntity);
        // entity.setObjectClass(objectClassEntity);
        areaoccupancyRepository.saveAndFlush(entity);
    }

}
