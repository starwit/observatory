package de.starwit.service.observatory;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import de.starwit.persistence.observatory.entity.JobType;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.persistence.observatory.entity.PointEntity;
import de.starwit.persistence.observatory.repository.ObservationJobRepository;
import de.starwit.persistence.observatory.repository.PointRepository;
import de.starwit.service.jobs.RunnerInterface;
import de.starwit.service.jobs.areaoccupancy.AreaOccupancyRunner;
import de.starwit.service.jobs.linecrossing.LineCrossingRunner;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ObservationJobService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${analytics.dataRetrievalRate:2000}")
    private int dataRetrievalRate;

    @Autowired
    private ObservationJobRepository observationJobRepository;

    @Lazy
    @Autowired
    private LineCrossingRunner lineCrossingRunner;

    @Lazy
    @Autowired
    private AreaOccupancyRunner areaOccupancyRunner;

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

    public List<ObservationJobEntity> findActiveAreaOccupancyJobs() {
        return observationJobRepository.findByEnabledTrueAndType(JobType.AREA_OCCUPANCY);
    }

    public List<ObservationJobEntity> findActiveLineCrossingJobs() {
        return observationJobRepository.findByEnabledTrueAndType(JobType.LINE_CROSSING);
    }

    public List<ObservationJobEntity> findActiveFlowJobs() {
        return observationJobRepository.findByEnabledTrueAndType(JobType.FLOW);
    }

    public List<ObservationJobEntity> findActiveJobs(JobType type) {
        return observationJobRepository.findByEnabledTrueAndType(type);
    }

    public ObservationJobEntity saveNew(ObservationJobEntity newJob) {
        newJob.getGeometryPoints().forEach(p -> p.setObservationJob(newJob));

        ObservationJobEntity savedEntity = observationJobRepository.save(newJob);
        
        refreshJobs();
        
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
        existingJob.setCenterLatitude(jobUpdate.getCenterLatitude());
        existingJob.setCenterLongitude(jobUpdate.getCenterLongitude());

        ObservationJobEntity updatedJob = observationJobRepository.save(existingJob);

        updatedJob.getGeometryPoints().forEach(p -> p.setObservationJob(updatedJob));

        pointRepository.deleteAll(oldPoints);

        refreshJobs();

        return updatedJob;
    }

    public void deleteById(Long id) {
        observationJobRepository.deleteById(id);

        refreshJobs();
    }

    public void deleteByObservationAreaId(Long observationAreaId) {
        observationJobRepository.deleteByObservationAreaId(observationAreaId);

        refreshJobs();
    }

    public void deleteAll() {
        observationJobRepository.deleteAll();
        refreshJobs();
    }

    private void refreshJobs() {
        lineCrossingRunner.refreshJobs(LineCrossingRunner.JOB_TYPE);
        areaOccupancyRunner.refreshJobs(AreaOccupancyRunner.JOB_TYPE);
    }
}
