package de.starwit.service.impl;
import java.util.List;
import de.starwit.persistence.entity.FlowEntity;
import de.starwit.persistence.repository.FlowRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 
 * Flow Service class
 *
 */
@Service
public class FlowService implements ServiceInterface<FlowEntity, FlowRepository> {

    @Autowired
    private FlowRepository flowRepository;

    @Override
    public FlowRepository getRepository() {
        return flowRepository;
    }

    public List<FlowEntity> findAllWithoutObjectClass() {
        return flowRepository.findAllWithoutObjectClass();
    }

    public List<FlowEntity> findAllWithoutOtherObjectClass(Long id) {
        return flowRepository.findAllWithoutOtherObjectClass(id);
    }

}
