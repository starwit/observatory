package de.starwit.service.analytics;

import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import de.starwit.persistence.analytics.entity.MetadataEntity;
import de.starwit.persistence.analytics.repository.AreaOccupancyRepository;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;

@Service
public class AreaOccupancyService {

    final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AreaOccupancyRepository areaoccupancyRepository;

    @Autowired
    private MetadataService metadataService;

    @Transactional("analyticsTransactionManager")
    public void addEntry(ObservationJobEntity jobEntity, ZonedDateTime occupancyTime, Long count) {
        log.info("Detected {} objects of class {} in area (area={}, name={})", count, jobEntity.getDetectionClassId(), jobEntity.getObservationAreaId(), jobEntity.getName());
        
        MetadataEntity metadata = metadataService.saveMetadataForJob(jobEntity);
        
        AreaOccupancyEntity entity = new AreaOccupancyEntity();
        entity.setCount(Math.toIntExact(count));
        entity.setOccupancyTime(occupancyTime);
        entity.setObjectClassId(jobEntity.getDetectionClassId());
        entity.setMetadataId(metadata.getId());
        areaoccupancyRepository.insert(entity);
    }

}
