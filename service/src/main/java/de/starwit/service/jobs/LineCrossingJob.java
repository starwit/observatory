package de.starwit.service.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.repository.SaeRepository;

@Component
public class LineCrossingJob extends AbstractJob<SaeDetectionEntity> {

    @Autowired
    private SaeRepository saeRepository;

    @Override
    List<SaeDetectionEntity> getData(JobData<SaeDetectionEntity> jobData) {
        return saeRepository.getLineCrossingDetectionData(jobData.getLastRetrievedTime(),
                jobData.getConfig().getCameraId(),
                jobData.getConfig().getDetectionClassId());
    }

    @Override
    void process(JobData<SaeDetectionEntity> jobData) {
        log.info("Processing data");
    }

}
