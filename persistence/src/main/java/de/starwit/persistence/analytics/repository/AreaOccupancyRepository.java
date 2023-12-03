package de.starwit.persistence.analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;

/**
 * AreaOccupancy Repository class
 */
@Repository
@Transactional(transactionManager = "analyticsTransactionManager", propagation = Propagation.REQUIRES_NEW)
public interface AreaOccupancyRepository extends JpaRepository<AreaOccupancyEntity, Long> {

}
