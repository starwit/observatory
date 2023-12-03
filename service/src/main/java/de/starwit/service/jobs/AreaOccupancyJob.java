package de.starwit.service.jobs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.common.entity.output.Result;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.service.analytics.AreaOccupancyService;

@Component
public class AreaOccupancyJob extends AbstractJob {

    @Autowired
    private AreaOccupancyService areaOccupancyService;

    @Override
    List<SaeDetectionEntity> getData(JobData jobData) {
        return new ArrayList<>();
        // return saeRepository.getDetectionData(jobData.getLastRetrievedTime(),
        // jobData.getConfig().getCameraId(),
        // jobData.getConfig().getDetectionClassId());
    }

    @Override
    List<? extends Result> process(JobData jobDate, List<SaeDetectionEntity> newData) {
        // unique object_ids for last ten seconds and every object classes
        areaOccupancyService.addEntry(null);
        return null;
    }
}
