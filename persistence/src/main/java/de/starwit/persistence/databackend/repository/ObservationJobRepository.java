package de.starwit.persistence.databackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.databackend.entity.ObservationJobEntity;

/**
 * ObjectClass Repository class
 */
@Repository
public interface ObservationJobRepository extends JpaRepository<ObservationJobEntity, Long> {

    List<ObservationJobEntity> findByEnabledTrue();
    
    void deleteByObservationAreaId(long observationAreaId);

}
