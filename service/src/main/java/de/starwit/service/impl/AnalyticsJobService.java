package de.starwit.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.starwit.persistence.entity.AnalyticsJobEntity;
import de.starwit.persistence.entity.PointEntity;
import de.starwit.persistence.repository.AnalyticsJobRepository;
import de.starwit.persistence.repository.PointRepository;
import de.starwit.service.analytics.AbstractJob;
import de.starwit.service.analytics.LineCrossingJob;
import de.starwit.service.datasource.SaeDataSource;
import jakarta.annotation.PostConstruct;

@Service
public class AnalyticsJobService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${analytics.dataRetrievalRate:2000}")
    private int dataRetrievalRate;

    @Autowired
    private AnalyticsJobRepository analyticsJobRepository;

    @Autowired
    private PointRepository pointRepository;

    private ScheduledExecutorService jobRunner;

    public List<AnalyticsJobEntity> findAll() {
        return analyticsJobRepository.findAll();
    }

    public AnalyticsJobEntity findById(Long id) {
        return analyticsJobRepository.findById(id).orElse(null);
    }

    public AnalyticsJobEntity saveNew(AnalyticsJobEntity newJob) {
        newJob.getGeometryPoints().forEach(p -> p.setAnalyticsJob(newJob));

        AnalyticsJobEntity savedEntity = analyticsJobRepository.save(newJob);

        refreshJobs();
        
        return savedEntity;
    }

    public AnalyticsJobEntity update(Long id, AnalyticsJobEntity jobUpdate) {
        AnalyticsJobEntity existingJob = this.findById(id);
        
        if (existingJob == null) {
            return null;
        }

        List<PointEntity> oldPoints = existingJob.getGeometryPoints();

        existingJob.setName(jobUpdate.getName());
        existingJob.setParkingAreaId(jobUpdate.getParkingAreaId());
        existingJob.setType(jobUpdate.getType());
        existingJob.setEnabled(jobUpdate.getEnabled());
        existingJob.setGeometryPoints(jobUpdate.getGeometryPoints());

        AnalyticsJobEntity updatedJob = analyticsJobRepository.save(existingJob);

        updatedJob.getGeometryPoints().forEach(p -> p.setAnalyticsJob(updatedJob));

        pointRepository.deleteAll(oldPoints);

        refreshJobs();

        return updatedJob;
    }

    public void deleteById(Long id) {
        analyticsJobRepository.deleteById(id);

        refreshJobs();
    }
    
    @PostConstruct
    public void refreshJobs() {
        this.stopJobFeeder();

        List<AbstractJob> jobsToRun = new ArrayList<>();
        List<AnalyticsJobEntity> enabledJobs = analyticsJobRepository.findByEnabledTrue();
        for (AnalyticsJobEntity jobConfig : enabledJobs) {
            jobsToRun.add(new LineCrossingJob(
                jobConfig, 
                new SaeDataSource(jobConfig.getCameraId(), jobConfig.getDetectionClassId())));
        }

        this.startJobRunner(jobsToRun);
    }

    public void startJobRunner(List<AbstractJob> jobs) {
        if (this.jobRunner != null && !this.jobRunner.isShutdown()) {
            log.info("Runner is already running.");
            return;
        }

        log.info("Starting job runner on an {}ms interval", dataRetrievalRate);
        log.info("Configured jobs: {}", jobs);

        this.jobRunner = Executors.newSingleThreadScheduledExecutor();
        this.jobRunner.scheduleAtFixedRate(() -> {

            for (AbstractJob job : jobs) {
                log.debug("Running job: {}", job.getConfig().getName());
                job.tick();
            }
        
        }, 1000, dataRetrievalRate, TimeUnit.MILLISECONDS);
    }

    public void stopJobFeeder() {
        if (this.jobRunner != null) {
            log.info("Stopping job feeder");
            this.jobRunner.shutdown();
        }
    }
}
