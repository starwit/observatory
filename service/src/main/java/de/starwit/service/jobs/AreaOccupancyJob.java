package de.starwit.service.jobs;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.starwit.persistence.sae.entity.SaeCountEntity;
import de.starwit.persistence.sae.repository.SaeRepository;
import de.starwit.service.analytics.AreaOccupancyService;

@Component
public class AreaOccupancyJob extends AbstractJob<SaeCountEntity> {

    @Autowired
    private AreaOccupancyService areaOccupancyService;

    @Autowired
    private SaeRepository saeRepository;

    @Override
    List<SaeCountEntity> getData(JobData<SaeCountEntity> jobData) {
        return saeRepository.getDetectionData(jobData.getLastRetrievedTime(),
                jobData.getConfig().getCameraId(),
                jobData.getConfig().getDetectionClassId());
    }

    @Override
    void process(JobData<SaeCountEntity> jobData) throws InterruptedException {
        if (jobData != null) {
            ArrayBlockingQueue<SaeCountEntity> queue = jobData.getInputData();
            while (queue != null && !queue.isEmpty()) {
                areaOccupancyService.addEntry(queue.take());
            }
        }
    }
}
