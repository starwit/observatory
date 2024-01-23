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
import de.starwit.persistence.analytics.repository.MetadataRepository;
import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;

/**
 * 
 * AreaOccupancy Service class
 *
 */
@Service
public class AreaOccupancyService {

    final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AreaOccupancyRepository areaoccupancyRepository;

    @Autowired
    private MetadataRepository metadataRepository;

    /**
     * Save Area occupancies for a minute.
     * 
     * @param saeEntry
     */
    @Transactional
    public void addEntry(AnalyticsJobEntity jobEntity, ZonedDateTime occupancyTime, Long count) {
        log.info("Detected {} objects of class {} in area (name={})", count, jobEntity.getDetectionClassId(), jobEntity.getName());
        
        MetadataEntity metadata = metadataRepository.findFirstByName(jobEntity.getName());

        if (metadata == null) {
            metadata = new MetadataEntity();
            metadata.setName(jobEntity.getName());
            metadata = metadataRepository.save(metadata);
        }
        
        AreaOccupancyEntity entity = new AreaOccupancyEntity();
        entity.setCount(Math.toIntExact(count));
        entity.setOccupancyTime(occupancyTime);
        entity.setParkingAreaId(jobEntity.getParkingAreaId());
        entity.setObjectClassId(jobEntity.getDetectionClassId());
        entity.setMetadataId(metadata.getId());
        areaoccupancyRepository.insert(entity);
    }

}
