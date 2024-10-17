package de.starwit.persistence.observatory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.observatory.entity.PointEntity;

/**
 * ObjectClass Repository class
 */
@Repository
public interface PointRepository extends JpaRepository<PointEntity, Long> {

}
