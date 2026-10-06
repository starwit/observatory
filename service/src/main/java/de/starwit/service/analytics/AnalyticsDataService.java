package de.starwit.service.analytics;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.analytics.entity.MetadataEntity;
import de.starwit.persistence.analytics.repository.AreaOccupancyRepository;
import de.starwit.persistence.analytics.repository.LineCrossingRepository;
import de.starwit.persistence.analytics.repository.MetadataRepository;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;

@Service
public class AnalyticsDataService {

    final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private LineCrossingRepository lineCrossingRepository;

    @Autowired
    private AreaOccupancyRepository areaOccupancyRepository;

    /**
     * Deletes all analytics data of the given job with a timestamp at or after the cutoff.
     * Covers all metadata versions matching the job's name and classification.
     */
    @Transactional("analyticsTransactionManager")
    public void deleteJobDataFrom(ObservationJobEntity jobEntity, Instant cutoff) {
        List<Long> metadataIds = metadataRepository
                .findByNameAndClassification(jobEntity.getName(), jobEntity.getClassification())
                .stream().map(MetadataEntity::getId).toList();

        if (metadataIds.isEmpty()) {
            log.info("No analytics data found for job (id={}, name={})", jobEntity.getId(), jobEntity.getName());
            return;
        }

        ZonedDateTime cutoffTs = cutoff.atZone(ZoneOffset.UTC);
        int lineCrossings = lineCrossingRepository.deleteByMetadataIdsFrom(metadataIds, cutoffTs);
        int areaOccupancies = areaOccupancyRepository.deleteByMetadataIdsAndObjectClassIdFrom(metadataIds,
                jobEntity.getDetectionClassId(), cutoffTs);

        log.info("Deleted {} line crossings and {} area occupancies at or after {} for job (id={}, name={})",
                lineCrossings, areaOccupancies, cutoff, jobEntity.getId(), jobEntity.getName());
    }
}
