package de.starwit.persistence.analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.analytics.entity.LineCrossingEntity;

/**
 * LineCrossing Repository class
 */
@Repository
public interface LineCrossingRepository extends JpaRepository<LineCrossingEntity, Long> {

}
