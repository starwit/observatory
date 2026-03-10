package de.starwit.service.analytics;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.analytics.entity.CoordinateEntity;
import de.starwit.persistence.analytics.entity.MetadataEntity;
import de.starwit.persistence.analytics.repository.MetadataRepository;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.persistence.observatory.entity.PointEntity;
import de.starwit.service.impl.ServiceInterface;

@Service
public class MetadataService implements ServiceInterface<MetadataEntity, MetadataRepository> {

    @Autowired
    MetadataRepository metadataRepository;

    @Autowired
    CoordinateService coordinateService;

    @Override
    public MetadataRepository getRepository() {
        return metadataRepository;
    }

    public MetadataEntity saveMetadataForJob(ObservationJobEntity jobEntity) {
        
        MetadataEntity metadata = findCurrentMetadata(jobEntity);

        if (metadata == null) {
            metadata = new MetadataEntity();
            metadata.setName(jobEntity.getName());
            metadata.setClassification(jobEntity.getClassification());
            metadata.setGeoReferenced(jobEntity.getGeoReferenced());
            metadata.setCenterLatitude(jobEntity.getCenterLatitude());
            metadata.setCenterLongitude(jobEntity.getCenterLongitude());
            metadata.setObservationAreaId(jobEntity.getObservationAreaId());
            metadata.setDirection(jobEntity.getDirection());

            List<CoordinateEntity> coordinates = new ArrayList<>();
            if (jobEntity.getGeoReferenced()) {
                coordinates = coordinateService.saveCoordinatesForJob(jobEntity);
                for (CoordinateEntity c : coordinates) {
                    c.setMetadata(metadata);
                }
            }
            metadata.setGeometryCoordinates(coordinates);
            
            metadata = metadataRepository.saveAndFlush(metadata);
        }

        return metadata;
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Delete is not supported");
    }

    public MetadataEntity findFirstByName(String name) {
        MetadataEntity metadata = metadataRepository.findFirstByName(name);
        return metadata;
    }

    public MetadataEntity findFirstByNameAndClassification(String name, String classification) {
        MetadataEntity metadata = metadataRepository.findFirstByNameAndClassification(name, classification);
        return metadata;
    }

    public MetadataEntity findCurrentMetadata(ObservationJobEntity jobEntity) {
        MetadataEntity latestMetadata = metadataRepository.findFirstByNameAndClassificationOrderByIdDesc(jobEntity.getName(), jobEntity.getClassification());
        if (latestMetadata != null && metadataMatchesJob(latestMetadata, jobEntity)) {
            return latestMetadata;
        } else {
            return null;
        }
    }

    /**
     * Checks if all metadata fields equal the corresponding job fields (i.e. whether given metadata entity belongs to the given job)
     */
    private boolean metadataMatchesJob(MetadataEntity metadata, ObservationJobEntity job) {
        if (metadata.getName() != null              && !metadata.getName().equals(job.getName()))                           return false;
        if (metadata.getClassification() != null    && !metadata.getClassification().equals(job.getClassification()))       return false;
        if (metadata.getGeoReferenced() != null     && !metadata.getGeoReferenced().equals(job.getGeoReferenced()))         return false;
        if (metadata.getCenterLatitude() != null    && !metadata.getCenterLatitude().equals(job.getCenterLatitude()))       return false;
        if (metadata.getCenterLongitude() != null   && !metadata.getCenterLongitude().equals(job.getCenterLongitude()))     return false;
        if (metadata.getObservationAreaId() != null && !metadata.getObservationAreaId().equals(job.getObservationAreaId())) return false;
        if (metadata.getDirection() != null         && !metadata.getDirection().equals(job.getDirection()))                 return false;

        // Check if geometry matches (only if geoReferenced is true, otherwise geometry is not relevant and can be ignored)
        if (job.getGeoReferenced()) {
            for (int i = 0; i < metadata.getGeometryCoordinates().size(); i++) {
                CoordinateEntity c1 = metadata.getGeometryCoordinates().get(i);
                PointEntity c2 = job.getGeometryPoints().get(i);
                if (c1.getLatitude() != null && !c1.getLatitude().equals(c2.getLatitude())) return false;
                if (c1.getLongitude() != null && !c1.getLongitude().equals(c2.getLongitude())) return false;
            }
        }

        return true;
    }
    
}
