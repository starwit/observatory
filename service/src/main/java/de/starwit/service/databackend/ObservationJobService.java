package de.starwit.service.databackend;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.persistence.databackend.entity.PointEntity;
import de.starwit.persistence.databackend.repository.ObservationJobRepository;
import de.starwit.persistence.databackend.repository.PointRepository;
import de.starwit.service.jobs.ObservationJobRunner;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ObservationJobService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${analytics.dataRetrievalRate:2000}")
    private int dataRetrievalRate;

    @Autowired
    private ObservationJobRepository observationJobRepository;

    @Autowired
    private ObservationJobRunner observationJobRunner;

    @Autowired
    private PointRepository pointRepository;

    public List<ObservationJobEntity> findAll() {
        return observationJobRepository.findAll();
    }

    public ObservationJobEntity findById(Long id) {
        return observationJobRepository.findById(id).orElse(null);
    }

    public List<ObservationJobEntity> findByEnabledTrue() {
        return observationJobRepository.findByEnabledTrue();
    }

    public ObservationJobEntity saveNew(ObservationJobEntity newJob) {
        newJob.getGeometryPoints().forEach(p -> p.setObservationJob(newJob));

        ObservationJobEntity savedEntity = observationJobRepository.save(newJob);
        
        observationJobRunner.refreshJobs();
        
        return savedEntity;
    }

    public ObservationJobEntity update(Long id, ObservationJobEntity jobUpdate) {
        ObservationJobEntity existingJob = this.findById(id);

        if (existingJob == null) {
            return null;
        }

        List<PointEntity> oldPoints = existingJob.getGeometryPoints();

        existingJob.setName(jobUpdate.getName());
        existingJob.setObservationAreaId(jobUpdate.getObservationAreaId());
        existingJob.setType(jobUpdate.getType());
        existingJob.setEnabled(jobUpdate.getEnabled());
        existingJob.setCameraId(jobUpdate.getCameraId());
        existingJob.setClassification(jobUpdate.getClassification());
        existingJob.setDetectionClassId(jobUpdate.getDetectionClassId());
        existingJob.setGeoReferenced(jobUpdate.getGeoReferenced());
        existingJob.setGeometryPoints(jobUpdate.getGeometryPoints());

        ObservationJobEntity updatedJob = observationJobRepository.save(existingJob);

        updatedJob.getGeometryPoints().forEach(p -> p.setObservationJob(updatedJob));

        pointRepository.deleteAll(oldPoints);

        observationJobRunner.refreshJobs();

        return updatedJob;
    }

    public void deleteById(Long id) {
        observationJobRepository.deleteById(id);

        observationJobRunner.refreshJobs();
    }

    public void deleteByObservationAreaId(Long observationAreaId) {
        observationJobRepository.deleteByObservationAreaId(observationAreaId);

        observationJobRunner.refreshJobs();
    }

    public void deleteAll() {
        observationJobRepository.deleteAll();

        observationJobRunner.refreshJobs();
    }
}
