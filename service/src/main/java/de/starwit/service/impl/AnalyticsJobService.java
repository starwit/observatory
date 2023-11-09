package de.starwit.service.impl;

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

@Service
public class AnalyticsJobService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AnalyticsJobRepository analyticsJobRepository;

    @Autowired
    private PointRepository pointRepository;

    private List<AnalyticsJobEntity> enabledJobs;
    private ScheduledExecutorService jobFeeder;

    @Value("${analytics.dataRetrievalRate:2000}")
    private int dataRetrievalRate;

    public List<AnalyticsJobEntity> findAll() {
        return analyticsJobRepository.findAll();
    }

    public AnalyticsJobEntity findById(Long id) {
        return analyticsJobRepository.findById(id).orElse(null);
    }

    public AnalyticsJobEntity saveNew(AnalyticsJobEntity newJob) {
        newJob.getGeometryPoints().forEach(p -> p.setAnalyticsJob(newJob));
        return analyticsJobRepository.save(newJob);
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
        
        return updatedJob;
    }

    public void deleteById(Long id) {
        analyticsJobRepository.deleteById(id);
    }
    
    public void refreshJobs() {
        this.enabledJobs = analyticsJobRepository.findByEnabledTrue();
    }

    public void startJobFeeder() {
        if (this.jobFeeder != null && !this.jobFeeder.isShutdown()) {
            log.info("Feeder is already running.");
            return;
        }

        log.info("Starting job feeder on an {}ms interval", dataRetrievalRate);

        this.jobFeeder = Executors.newSingleThreadScheduledExecutor();
        this.jobFeeder.scheduleAtFixedRate(() -> {

            // Get latest data (and make sure we do not read any datapoint twice)
            log.info("Getting new data");
        
            // Feed new data into all analytics jobs
            for (AnalyticsJobEntity job : this.enabledJobs) {
                log.info("Feeding job: {}", job.getName());
            }
        
            // All jobs write their results into the output db on their own?
        }, 0, dataRetrievalRate, TimeUnit.MILLISECONDS);
    }

    public void stopJobFeeder() {
        this.jobFeeder.shutdown();
    }
}
