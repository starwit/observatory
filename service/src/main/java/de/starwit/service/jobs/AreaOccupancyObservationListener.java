package de.starwit.service.jobs;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.persistence.databackend.entity.ObservationJobEntity;

public class AreaOccupancyObservationListener {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ArrayBlockingQueue<AreaOccupancyObservation> outputQueue = new ArrayBlockingQueue<>(1000);
    
    public void onObservation(ObservationJobEntity jobEntity, ZonedDateTime occupancyTime, Long count) {
        boolean success = this.outputQueue.offer(new AreaOccupancyObservation(jobEntity, occupancyTime, count));
        if (!success) {
            log.warn("Discarding observation due to queue capacity");
        }
    }

    public List<AreaOccupancyObservation> getBufferedMessages() {
        List<AreaOccupancyObservation> resultList = new ArrayList<>();
        outputQueue.drainTo(resultList);
        return resultList;
    }
}
