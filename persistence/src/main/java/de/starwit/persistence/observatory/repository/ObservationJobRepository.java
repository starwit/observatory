package de.starwit.persistence.observatory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.observatory.entity.JobType;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;

/**
 * ObjectClass Repository class
 */
@Repository
public interface ObservationJobRepository extends JpaRepository<ObservationJobEntity, Long> {

    List<ObservationJobEntity> findByEnabledTrue();

    List<ObservationJobEntity> findByEnabledTrueAndType(JobType type);
    
    void deleteByObservationAreaId(long observationAreaId);

}
