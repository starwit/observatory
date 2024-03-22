package de.starwit.service.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.analytics.entity.MetadataEntity;
import de.starwit.persistence.analytics.repository.MetadataRepository;
import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;
import de.starwit.service.impl.ServiceInterface;

@Service
public class MetadataService implements ServiceInterface<MetadataEntity, MetadataRepository> {

    @Autowired
    MetadataRepository metadataRepository;

    @Override
    public MetadataRepository getRepository() {
        return metadataRepository;
    }

    public MetadataEntity getMetadataForJob(AnalyticsJobEntity jobEntity) {
        
        MetadataEntity metadata = metadataRepository.findFirstByNameAndClassification(jobEntity.getName(), jobEntity.getClassification());

        if (metadata == null) {
            metadata = new MetadataEntity();
            metadata.setName(jobEntity.getName());
            metadata.setClassification(jobEntity.getClassification());
            metadata = metadataRepository.save(metadata);
        }

        return metadata;
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Delete is not supported");
    }
    
}
