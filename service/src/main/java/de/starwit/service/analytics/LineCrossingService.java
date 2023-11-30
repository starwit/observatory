package de.starwit.service.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.analytics.entity.LineCrossingEntity;
import de.starwit.persistence.analytics.repository.LineCrossingRepository;
import de.starwit.service.impl.ServiceInterface;

/**
 * 
 * LineCrossing Service class
 *
 */
@Service
public class LineCrossingService implements ServiceInterface<LineCrossingEntity, LineCrossingRepository> {

    @Autowired
    private LineCrossingRepository linecrossingRepository;

    @Override
    public LineCrossingRepository getRepository() {
        return linecrossingRepository;
    }
}
