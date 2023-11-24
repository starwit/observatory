package de.starwit.service.analytics;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.persistence.entity.AnalyticsJobEntity;
import de.starwit.persistence.entity.output.Result;
import de.starwit.service.datasource.SaeDataSource;

public class AreaOccupancyJob extends AbstractJob {

    public AreaOccupancyJob(AnalyticsJobEntity config, SaeDataSource dataSource) {
        super(config, dataSource);
    }

    final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    List<? extends Result> process() {
        log.info("Processing data");
        log.info("TESTTESTTESTTEST");
        return null;
    }

}
