package de.starwit.service.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.analytics.entity.ObjectClassEntity;
import de.starwit.persistence.analytics.repository.ObjectClassRepository;
import de.starwit.service.impl.ServiceInterface;

/**
 * 
 * ObjectClass Service class
 *
 */
@Service
public class ObjectClassService implements ServiceInterface<ObjectClassEntity, ObjectClassRepository> {

    @Autowired
    private ObjectClassRepository objectclassRepository;

    @Override
    public ObjectClassRepository getRepository() {
        return objectclassRepository;
    }

}
