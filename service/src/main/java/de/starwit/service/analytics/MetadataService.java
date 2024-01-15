package de.starwit.service.analytics;

import org.springframework.beans.factory.annotation.Autowired;

import de.starwit.persistence.analytics.entity.MetadataEntity;
import de.starwit.persistence.analytics.repository.MetadataRepository;
import de.starwit.service.impl.ServiceInterface;

public class MetadataService implements ServiceInterface<MetadataEntity, MetadataRepository> {

    @Autowired
    MetadataRepository metadataRepository;

    @Override
    public MetadataRepository getRepository() {
        return metadataRepository;
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Delete is not supported");
    }
    
}
