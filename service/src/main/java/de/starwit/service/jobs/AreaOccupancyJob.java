package de.starwit.service.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.common.entity.output.Result;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.repository.SaeRepository;

@Service
public class AreaOccupancyJob extends AbstractJob {

    @Autowired
    private SaeRepository saeRepository;

    @Override
    List<SaeDetectionEntity> getData(JobData jobData) {
        return saeRepository.getDetectionData(jobData.getLastRetrievedTime(), jobData.getConfig().getCameraId(),
                jobData.getConfig().getDetectionClassId());
    }

    @Override
    List<? extends Result> process(JobData jobDate, List<SaeDetectionEntity> newData) {
        // unique object_ids for last ten seconds and every object classes
        return null;
    }
}
