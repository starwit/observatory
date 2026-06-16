package de.starwit.service.analytics;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import de.starwit.persistence.analytics.entity.Direction;
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

    @Value("${areaOccupancy.flowIdleDecreaseEnabled:false}")
    private boolean FLOW_IDLE_DECREASE_ENABLED;

    @Value("${areaOccupancy.flowIdleDecreaseThreshold:1h}")
    private Duration FLOW_IDLE_DECREASE_THRESHOLD;

    @Value("${areaOccupancy.flowIdleDecreaseInterval:15m}")
    private Duration FLOW_IDLE_DECREASE_INTERVAL;

    @Value("${areaOccupancy.flowIdleDecreaseFactor:1}")
    private int FLOW_IDLE_DECREASE_FACTOR;

    @Transactional("analyticsTransactionManager")
    public void addEntry(ObservationJobEntity jobEntity, ZonedDateTime occupancyTime, Long count) {
        log.info("Detected {} objects of class {} in area (area={}, name={})", count, jobEntity.getDetectionClassId(),
                jobEntity.getObservationAreaId(), jobEntity.getName());

        MetadataEntity metadata = metadataService.saveMetadataForJob(jobEntity);

        AreaOccupancyEntity entity = new AreaOccupancyEntity();
        entity.setCount(Math.toIntExact(count));
        entity.setOccupancyTime(occupancyTime);
        entity.setObjectClassId(jobEntity.getDetectionClassId());
        entity.setMetadataId(metadata.getId());
        areaoccupancyRepository.insert(entity);
    }

    @Transactional("analyticsTransactionManager")
    public AreaOccupancyEntity setOccupancyCount(String jobName, int class_id, Long count) {
        MetadataEntity metadata = metadataService.findFirstByName(jobName);
        if (metadata != null) {
            AreaOccupancyEntity entity = new AreaOccupancyEntity();
            entity.setCount(Math.toIntExact(count));
            entity.setOccupancyTime(Instant.now().atZone(ZoneOffset.UTC));
            entity.setObjectClassId(class_id);
            entity.setMetadataId(metadata.getId());
            areaoccupancyRepository.insert(entity);
            return entity;
        }
        return null;
    }

    @Transactional("analyticsTransactionManager")
    public void updateCountFromFlow(ObservationJobEntity jobEntity, ZonedDateTime occupancyTime, Direction direction) {
        log.info("Detected flow in direction {} of class {} in area (area={}, name={})", direction,
                jobEntity.getDetectionClassId(), jobEntity.getObservationAreaId(), jobEntity.getName());

        MetadataEntity metadata = metadataService.saveMetadataForJob(jobEntity);

        AreaOccupancyEntity lastEntity = areaoccupancyRepository
                .findFirstByMetadataIdAndObjectClassIdOrderByOccupancytime(metadata.getId(),
                        jobEntity.getDetectionClassId());
        int lastCount = lastEntity == null ? 0 : lastEntity.getCount();

        int newCount;
        if (Direction.in.equals(direction)) {
            if (jobEntity.getMaxCount() == null || lastCount < jobEntity.getMaxCount()) {
                newCount = lastCount + 1;
            } else {
                log.info("Max count of {} for job {} reached. Resetting to max count.", jobEntity.getMaxCount(),
                        jobEntity.getName());
                newCount = jobEntity.getMaxCount();
            }
        } else if (lastCount > 0) {
            newCount = lastCount - 1;
        } else {
            newCount = 0;
        }

        // Slowly pull count towards zero after times of no activity (assumption is that area is empty if no activity)
        if (FLOW_IDLE_DECREASE_ENABLED && lastEntity != null) {
            Duration elapsed = Duration.between(lastEntity.getOccupancyTime(), occupancyTime);
            if (elapsed.compareTo(FLOW_IDLE_DECREASE_THRESHOLD) > 0) {
                long steps = elapsed.dividedBy(FLOW_IDLE_DECREASE_INTERVAL);
                newCount = Math.max(0, newCount - Math.toIntExact(steps * FLOW_IDLE_DECREASE_FACTOR));
            }
        }

        AreaOccupancyEntity entity = new AreaOccupancyEntity();
        entity.setCount(newCount);
        entity.setOccupancyTime(occupancyTime);
        entity.setObjectClassId(jobEntity.getDetectionClassId());
        entity.setMetadataId(metadata.getId());
        areaoccupancyRepository.insert(entity);
    }

}
