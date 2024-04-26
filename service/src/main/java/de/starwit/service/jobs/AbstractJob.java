package de.starwit.service.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

public abstract class AbstractJob {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected ObservationJobEntity configEntity;
    
    public AbstractJob(ObservationJobEntity configEntity) {
        this.configEntity = configEntity;
    }

    protected abstract void processNewDetection(SaeDetectionDto dto);

    public ObservationJobEntity getConfigEntity() {
        return this.configEntity;
    }

}
