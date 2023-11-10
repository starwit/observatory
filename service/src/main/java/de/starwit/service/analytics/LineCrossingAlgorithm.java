package de.starwit.service.analytics;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import de.starwit.persistence.entity.output.LineCrossingResult;
import de.starwit.service.datasource.SaeDetectionDTO;

public class LineCrossingAlgorithm implements Algorithm {

    @Override
    public List<LineCrossingResult> process(ArrayBlockingQueue<SaeDetectionDTO> inputData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'process'");
    }
    
}
