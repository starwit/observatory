package de.starwit.service.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.sae.entity.SaeCountEntity;
import de.starwit.persistence.sae.repository.SaeRepository;

@Component
public class LineCrossingJob extends AbstractJob<SaeCountEntity> {

    @Autowired
    private SaeRepository saeRepository;

    @Override
    List<SaeCountEntity> getData(JobData<SaeCountEntity> jobData) {
        return saeRepository.getDetectionData(jobData.getLastRetrievedTime(), jobData.getConfig().getCameraId(),
                jobData.getConfig().getDetectionClassId());
    }

    @Override
    void process(JobData<SaeCountEntity> jobData) {
        log.info("Processing data");
    }

}
