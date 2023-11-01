package de.starwit.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import de.starwit.persistence.entity.FlowEntity;

/**
 * Flow Repository class
 */
@Repository
public interface FlowRepository extends JpaRepository<FlowEntity, Long> {

    @Query("SELECT e FROM FlowEntity e WHERE NOT EXISTS (SELECT r FROM e.objectClass r)")
    public List<FlowEntity> findAllWithoutObjectClass();

    @Query("SELECT e FROM FlowEntity e WHERE NOT EXISTS (SELECT r FROM e.objectClass r WHERE r.id <> ?1)")
    public List<FlowEntity> findAllWithoutOtherObjectClass(Long id);
}
