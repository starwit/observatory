package de.starwit.service.analytics;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import de.starwit.persistence.entity.input.SaeInput;
import de.starwit.persistence.entity.output.Result;

public interface Algorithm {

    public List<? extends Result> process(ArrayBlockingQueue<SaeInput> inputData);
    
}
