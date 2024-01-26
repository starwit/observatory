package de.starwit.persistence.analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.analytics.entity.MetadataEntity;

@Repository
public interface MetadataRepository extends JpaRepository<MetadataEntity, Long> {

    public MetadataEntity findFirstByName(String name);

    public MetadataEntity findFirstByNameAndClassification(String name, String classification);

}
