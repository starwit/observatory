package de.starwit.service.analytics;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import de.starwit.persistence.analytics.repository.AreaOccupancyRepository;
import de.starwit.service.impl.ServiceInterface;

/**
 * 
 * AreaOccupancy Service class
 *
 */
@Service
public class AreaOccupancyService implements ServiceInterface<AreaOccupancyEntity, AreaOccupancyRepository> {

    @Autowired
    private AreaOccupancyRepository areaoccupancyRepository;

    @Override
    public AreaOccupancyRepository getRepository() {
        return areaoccupancyRepository;
    }

}
