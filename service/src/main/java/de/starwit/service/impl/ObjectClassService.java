package de.starwit.service.impl;
import de.starwit.persistence.entity.ObjectClassEntity;
import de.starwit.persistence.repository.FlowRepository;
import de.starwit.persistence.repository.ObjectClassRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import de.starwit.persistence.entity.FlowEntity;

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
    private FlowRepository flowRepository;

    @Override
    public ObjectClassRepository getRepository() {
        return objectclassRepository;
    }


    @Override
    public ObjectClassEntity saveOrUpdate(ObjectClassEntity entity) {

        Set<FlowEntity> flowToSave = entity.getFlow();

        if (entity.getId() != null) {
            ObjectClassEntity entityPrev = this.findById(entity.getId());
            for (FlowEntity item : entityPrev.getFlow()) {
                FlowEntity existingItem = flowRepository.getById(item.getId());
                existingItem.setObjectClass(null);
                this.flowRepository.save(existingItem);
            }
        }

        entity.setFlow(null);
        entity = this.getRepository().save(entity);
        this.getRepository().flush();

        if (flowToSave != null && !flowToSave.isEmpty()) {
            for (FlowEntity item : flowToSave) {
                FlowEntity newItem = flowRepository.getById(item.getId());
                newItem.setObjectClass(entity);
                flowRepository.save(newItem);
            }
        }
        return this.getRepository().getById(entity.getId());
    }
}
