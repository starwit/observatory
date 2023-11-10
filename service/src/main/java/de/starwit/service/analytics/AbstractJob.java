package de.starwit.service.analytics;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.persistence.entity.AnalyticsJobEntity;
import de.starwit.persistence.entity.output.Result;
import de.starwit.service.datasource.SaeDetectionDTO;
import de.starwit.service.datasource.SaeDataSource;

public abstract class AbstractJob {
    final Logger log = LoggerFactory.getLogger(this.getClass());

    private ArrayBlockingQueue<SaeDetectionDTO> inputData;
    private final AnalyticsJobEntity config;
    private final SaeDataSource dataSource;

    public AbstractJob(AnalyticsJobEntity config, SaeDataSource dataSource) {
        this.inputData = new ArrayBlockingQueue<>(100);
        this.config = config;
        this.dataSource = dataSource;
    }

    public void tick() {
        List<SaeDetectionDTO> newData = this.dataSource.getNewData();

        int discardCount = 0;
        boolean success = false;
        for (int i = newData.size() - 1; i >= 0; i--) {
            success = this.inputData.offer(newData.get(i));
            if (!success) {
                discardCount++;
            }
        }
        if (discardCount > 0) {
            log.warn("Discarded {} elements", discardCount);
        }

        List<? extends Result> results = this.process();
        
        // Pass data to database output / writer
    }

    abstract List<? extends Result> process();

    public AnalyticsJobEntity getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return String.format(
                "Job(type=%s, name=%s, parkingareaid=%s)",
                this.config.getType(),
                this.config.getName(),
                this.config.getParkingAreaId());
    }
}
