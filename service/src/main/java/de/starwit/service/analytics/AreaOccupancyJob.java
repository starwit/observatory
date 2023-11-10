package de.starwit.service.analytics;

import java.util.List;

import de.starwit.persistence.entity.AnalyticsJobEntity;
import de.starwit.persistence.entity.output.Result;
import de.starwit.service.datasource.SaeDataSource;

public class AreaOccupancyJob extends AbstractJob {

    public AreaOccupancyJob(AnalyticsJobEntity config, SaeDataSource dataSource) {
        super(config, dataSource);
    }

    @Override
    List<? extends Result> process() {
        log.info("Processing data");
        return null;
    }
    
}
