package de.starwit.persistence.databackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.databackend.entity.PointEntity;

/**
 * ObjectClass Repository class
 */
@Repository
public interface PointRepository extends JpaRepository<PointEntity, Long> {

}
