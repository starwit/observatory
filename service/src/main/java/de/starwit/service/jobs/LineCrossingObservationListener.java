package de.starwit.service.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

public class LineCrossingObservationListener {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ArrayBlockingQueue<LineCrossingObservation> outputQueue = new ArrayBlockingQueue<>(1000);
    
    public void onObservation(SaeDetectionDto det, Direction direction, ObservationJobEntity jobEntity) {
        boolean success = this.outputQueue.offer(new LineCrossingObservation(det, direction, jobEntity));
        if (!success) {
            log.warn("Discarding observation due to queue capacity");
        }
    }

    public List<LineCrossingObservation> getBufferedMessages() {
        List<LineCrossingObservation> resultList = new ArrayList<>();
        outputQueue.drainTo(resultList);
        return resultList;
    }
}
