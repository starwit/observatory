package de.starwit.service.databackend;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;
import de.starwit.persistence.databackend.entity.PointEntity;
import de.starwit.persistence.databackend.repository.AnalyticsJobRepository;
import de.starwit.persistence.databackend.repository.PointRepository;

@Service
public class AnalyticsJobService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${analytics.dataRetrievalRate:2000}")
    private int dataRetrievalRate;

    @Autowired
    private AnalyticsJobRepository analyticsJobRepository;

    @Autowired
    private PointRepository pointRepository;

    public List<AnalyticsJobEntity> findAll() {
        return analyticsJobRepository.findAll();
    }

    public AnalyticsJobEntity findById(Long id) {
        return analyticsJobRepository.findById(id).orElse(null);
    }

    public List<AnalyticsJobEntity> findByEnabledTrue() {
        return analyticsJobRepository.findByEnabledTrue();
    }

    public AnalyticsJobEntity saveNew(AnalyticsJobEntity newJob) {
        newJob.getGeometryPoints().forEach(p -> p.setAnalyticsJob(newJob));

        AnalyticsJobEntity savedEntity = analyticsJobRepository.save(newJob);
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
        return updatedJob;
    }

    public void deleteById(Long id) {
        analyticsJobRepository.deleteById(id);
    }

    public void deleteAll() {
        analyticsJobRepository.deleteAll();
    }
}
