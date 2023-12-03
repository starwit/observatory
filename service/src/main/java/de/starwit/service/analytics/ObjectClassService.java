package de.starwit.service.analytics;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.analytics.entity.LineCrossingEntity;
import de.starwit.persistence.analytics.entity.ObjectClassEntity;
import de.starwit.persistence.analytics.repository.LineCrossingRepository;
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

    @Autowired
    private LineCrossingRepository linecrossingRepository;

    @Override
    public ObjectClassRepository getRepository() {
        return objectclassRepository;
    }

    @Override
    public ObjectClassEntity saveOrUpdate(ObjectClassEntity entity) {

        Set<LineCrossingEntity> flowToSave = entity.getFlow();

        if (entity.getId() != null) {
            ObjectClassEntity entityPrev = this.findById(entity.getId());
            for (LineCrossingEntity item : entityPrev.getFlow()) {
                LineCrossingEntity existingItem = linecrossingRepository.getReferenceById(item.getId());
                existingItem.setObjectClass(null);
                this.linecrossingRepository.save(existingItem);
            }
        }

        entity.setFlow(null);
        entity = this.getRepository().save(entity);
        this.getRepository().flush();

        if (flowToSave != null && !flowToSave.isEmpty()) {
            for (LineCrossingEntity item : flowToSave) {
                LineCrossingEntity newItem = linecrossingRepository.getReferenceById(item.getId());
                newItem.setObjectClass(entity);
                linecrossingRepository.save(newItem);
            }
        }
        return this.getRepository().getReferenceById(entity.getId());
    }
}
