package de.starwit.service.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.analytics.repository.LineCrossingRepository;
import de.starwit.persistence.sae.entity.SaeCountEntity;

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
    public void addEntry(SaeCountEntity entity) {
        linecrossingRepository.insert(entity);
    }
}
