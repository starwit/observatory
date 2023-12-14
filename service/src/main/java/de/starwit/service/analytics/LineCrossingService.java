package de.starwit.service.analytics;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.analytics.entity.LineCrossingEntity;
import de.starwit.persistence.analytics.repository.LineCrossingRepository;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;

/**
 * 
 * LineCrossing Service class
 *
 */
@Service
public class LineCrossingService {

    @Autowired
    private LineCrossingRepository linecrossingRepository;

    @Transactional
    public void addEntry(SaeDetectionEntity det, Long parkingAreaId, Direction direction) {
        LineCrossingEntity entity = new LineCrossingEntity();
        entity.setCrossingTime(ZonedDateTime.ofInstant(det.getCaptureTs(), ZoneId.systemDefault()));
        entity.setDirection(direction);
        entity.setObjectClassId(det.getClassId());
        entity.setObjectId(det.getObjectId());
        entity.setParkingAreaId(parkingAreaId);
        linecrossingRepository.insert(entity);
    }
}
