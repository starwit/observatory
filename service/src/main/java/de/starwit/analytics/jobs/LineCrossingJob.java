package de.starwit.analytics.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.persistence.common.entity.output.Result;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.repository.SaeRepository;

@Service
public class LineCrossingJob extends AbstractJob {

    @Autowired
    private SaeRepository analyticsDao;

    @Override
    List<SaeDetectionEntity> getData(JobData jobData) {
        return analyticsDao.getDetectionData(jobData.getLastRetrievedTime(), jobData.getConfig().getCameraId(),
                jobData.getConfig().getDetectionClassId());
    }

    @Override
    List<? extends Result> process(JobData jobData) {
        log.info("Processing data");
        return null;
    }

}
