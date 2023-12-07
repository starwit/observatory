package de.starwit.persistence.analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.analytics.entity.ObjectClassEntity;

/**
 * ObjectClass Repository class
 */
@Repository
public interface ObjectClassRepository extends JpaRepository<ObjectClassEntity, Long> {

}
