package de.starwit.service.analytics;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import de.starwit.persistence.entity.output.Result;
import de.starwit.service.datasource.SaeDetectionDTO;

public interface Algorithm {

    public List<? extends Result> process(ArrayBlockingQueue<SaeDetectionDTO> inputData);
    
}
