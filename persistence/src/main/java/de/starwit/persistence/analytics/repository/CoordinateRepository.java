package de.starwit.persistence.analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.analytics.entity.CoordinateEntity;

@Repository
public interface CoordinateRepository extends JpaRepository<CoordinateEntity, Long> {

}