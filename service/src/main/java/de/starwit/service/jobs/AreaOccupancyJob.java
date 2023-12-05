package de.starwit.service.jobs;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.common.entity.output.Result;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.repository.SaeRepository;
import de.starwit.service.analytics.AreaOccupancyService;

@Component
public class AreaOccupancyJob extends AbstractJob {

    @Autowired
    private AreaOccupancyService areaOccupancyService;

    @Autowired
    private SaeRepository saeRepository;

    @Override
    List<SaeDetectionEntity> getData(JobData jobData) {
        return saeRepository.getDetectionData(jobData.getLastRetrievedTime(),
                jobData.getConfig().getCameraId(),
                jobData.getConfig().getDetectionClassId());
    }

    @Override
    List<? extends Result> process(JobData jobData) throws InterruptedException {
        if (jobData != null) {
            ArrayBlockingQueue<SaeDetectionEntity> queue = jobData.getInputData();
            while (queue != null && !queue.isEmpty()) {
                areaOccupancyService.addEntry(queue.take());
            }
        }
        return null;
    }
}
