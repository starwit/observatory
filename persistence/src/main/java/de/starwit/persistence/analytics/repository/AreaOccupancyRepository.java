package de.starwit.persistence.analytics.repository;

import java.time.ZonedDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import java.util.List;

/**
 * AreaOccupancy Repository class
 */
@Repository
public interface AreaOccupancyRepository extends JpaRepository<AreaOccupancyEntity, Long> {

    @Query(value = "SELECT COUNT(a.objectId) FROM areaoccupancy a WHERE a.occupancytime = :startTime", nativeQuery = true)
    long countDetectionId(@Param("startTime") ZonedDateTime startTime);

    @Query(value = "SELECT a FROM areaoccupancy a WHERE a.occupancytime = :startTime and a.objectclass_id = :objectClassId", nativeQuery = true)
    List<AreaOccupancyEntity> findByOccupancyTimeAndObjectClass(@Param("startTime") ZonedDateTime occupancyTime,
            @Param("objectClassId") Integer objectClassId);

}
