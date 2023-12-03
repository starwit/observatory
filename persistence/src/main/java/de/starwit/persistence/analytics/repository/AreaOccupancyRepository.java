package de.starwit.persistence.analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;

/**
 * AreaOccupancy Repository class
 */
@Repository
public interface AreaOccupancyRepository extends JpaRepository<AreaOccupancyEntity, Long> {

}
