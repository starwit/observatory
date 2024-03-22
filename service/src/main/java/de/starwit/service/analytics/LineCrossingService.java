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
import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;

@Service
public class LineCrossingService {

    final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LineCrossingRepository linecrossingRepository;

    @Autowired
    private MetadataService metadataService;

    @Transactional
    public void addEntry(SaeDetectionEntity det, Direction direction, AnalyticsJobEntity jobEntity) {
        log.info("{} has crossed line (name={}) in direction {}", det.getObjectId(), jobEntity.getName(), direction);
        
        MetadataEntity metadata = metadataService.getMetadataForJob(jobEntity);
        
        LineCrossingEntity entity = new LineCrossingEntity();
        entity.setCrossingTime(ZonedDateTime.ofInstant(det.getCaptureTs(), ZoneId.systemDefault()));
        entity.setDirection(direction);
        entity.setObjectClassId(det.getClassId().longValue());
        entity.setObjectId(det.getObjectId());
        entity.setObservationAreaId(jobEntity.getParkingAreaId());
        entity.setMetadataId(metadata.getId());
        linecrossingRepository.insert(entity);
    }
}
