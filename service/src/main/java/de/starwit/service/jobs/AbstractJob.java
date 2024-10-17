package de.starwit.service.jobs;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

public abstract class AbstractJob {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Executor jobExecutor = Executors.newSingleThreadExecutor();

    protected ObservationJobEntity configEntity;
    
    public AbstractJob(ObservationJobEntity configEntity) {
        this.configEntity = configEntity;
    }

    /**
     * Submits a new detection to the job executor (jobs handle their processing in a separate thread)
     * @param dto
     */
    public void pushNewDetection(SaeDetectionDto dto) {
        jobExecutor.execute(() -> processNewDetection(dto));
    }

    /**
     * Processes a new detection, i.e. does the actual analysis. Is being run in sequence on a separate thread.
     * @param dto
     */
    protected abstract void processNewDetection(SaeDetectionDto dto);

    public ObservationJobEntity getConfigEntity() {
        return this.configEntity;
    }

}
