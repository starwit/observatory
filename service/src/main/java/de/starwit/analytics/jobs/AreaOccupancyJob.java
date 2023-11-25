package de.starwit.analytics.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.starwit.analytics.dtos.SaeDetectionDto;
import de.starwit.analytics.services.AnalyticsDao;
import de.starwit.persistence.entity.output.Result;

@Service
public class AreaOccupancyJob extends AbstractJob {

    @Autowired
    private AnalyticsDao analyticsDao;

    @Override
    List<SaeDetectionDto> getData(JobData jobData) {
        return analyticsDao.getDetectionData(jobData.getLastRetrievedTime(), jobData.getConfig().getCameraId(),
                jobData.getConfig().getDetectionClassId());
    }

    @Override
    List<? extends Result> process(JobData jobDate) {
        log.info("Processing data");
        log.info("TESTTESTTESTTEST");
        return null;
    }
}
