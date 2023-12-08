package de.starwit.service.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.analytics.repository.AreaOccupancyRepository;
import de.starwit.persistence.sae.entity.SaeCountEntity;

/**
 * 
 * AreaOccupancy Service class
 *
 */
@Service
public class AreaOccupancyService {

    @Autowired
    private AreaOccupancyRepository areaoccupancyRepository;

    /**
     * Save Area occupancies for a minute.
     * 
     * @param saeEntry
     */
    @Transactional
    public void addEntry(SaeCountEntity entity) {
        areaoccupancyRepository.insert(entity);
    }

}
