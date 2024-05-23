package de.starwit.service.analytics;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.analytics.entity.LineCrossingEntity;
import de.starwit.persistence.analytics.entity.MetadataEntity;
import de.starwit.persistence.analytics.repository.LineCrossingRepository;
import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

@Service
public class LineCrossingService {

    final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LineCrossingRepository linecrossingRepository;

    @Autowired
    private MetadataService metadataService;

    @Transactional("analyticsTransactionManager")
    public void addEntry(SaeDetectionDto det, Direction direction, ObservationJobEntity jobEntity) {
        log.info("{} has crossed line (area={}, name={}) in direction {}", det.getObjectId(), jobEntity.getObservationAreaId(), jobEntity.getName(), direction);
        
        MetadataEntity metadata = metadataService.saveMetadataForJob(jobEntity);
        
        LineCrossingEntity entity = new LineCrossingEntity();
        entity.setCrossingTime(ZonedDateTime.ofInstant(det.getCaptureTs(), ZoneId.systemDefault()));
        entity.setDirection(direction);
        entity.setObjectClassId(det.getClassId().longValue());
        entity.setObjectId(det.getObjectId());
        entity.setObservationAreaId(jobEntity.getObservationAreaId());
        entity.setMetadataId(metadata.getId());
        linecrossingRepository.insert(entity);
    }
}
