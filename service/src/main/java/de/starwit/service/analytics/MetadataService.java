package de.starwit.service.analytics;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.analytics.entity.CoordinateEntity;
import de.starwit.persistence.analytics.entity.MetadataEntity;
import de.starwit.persistence.analytics.repository.MetadataRepository;
import de.starwit.persistence.databackend.entity.ObservationJobEntity;
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

    public MetadataEntity getMetadataForJob(ObservationJobEntity jobEntity) {
        
        MetadataEntity metadata = metadataRepository.findFirstByNameAndClassification(jobEntity.getName(), jobEntity.getClassification());

        if (metadata == null) {
            List<CoordinateEntity> coordinates = new ArrayList<>();
            if (jobEntity.getGeoReferenced()) {
                coordinates = coordinateService.getCoordinatesForJob(jobEntity);
            }
            metadata = new MetadataEntity();
            metadata.setName(jobEntity.getName());
            metadata.setClassification(jobEntity.getClassification());
            metadata.setGeoReferenced(jobEntity.getGeoReferenced());
            metadata.setGeometryCoordinates(coordinates);
            metadata = metadataRepository.saveAndFlush(metadata);
        }

        return metadata;
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Delete is not supported");
    }
    
}
