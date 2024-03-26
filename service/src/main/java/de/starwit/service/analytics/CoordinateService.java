package de.starwit.service.analytics;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.analytics.entity.CoordinateEntity;
import de.starwit.persistence.analytics.repository.CoordinateRepository;
import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.persistence.databackend.entity.PointEntity;
import de.starwit.service.impl.ServiceInterface;

@Service
public class CoordinateService implements ServiceInterface<CoordinateEntity, CoordinateRepository> {

    @Autowired
    private CoordinateRepository coordinateRepository;
    
    @Override
    public CoordinateRepository getRepository() {
        return this.coordinateRepository;
    }

    public List<CoordinateEntity> saveCoordinatesForJob(ObservationJobEntity jobEntity) {
        List<CoordinateEntity> savedEntities = new ArrayList<>();

        for (PointEntity point : jobEntity.getGeometryPoints()) {
            CoordinateEntity coordinate = new CoordinateEntity();
            coordinate.setLatitude(point.getLatitude());
            coordinate.setLongitude(point.getLongitude());
            savedEntities.add(coordinateRepository.saveAndFlush(coordinate));
        }

        return savedEntities;
    }
    
}
