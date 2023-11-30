package de.starwit.persistence.databackendconfig.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.databackendconfig.entity.AnalyticsJobEntity;

/**
 * ObjectClass Repository class
 */
@Repository
public interface AnalyticsJobRepository extends JpaRepository<AnalyticsJobEntity, Long> {

    List<AnalyticsJobEntity> findByEnabledTrue();

}
