package de.starwit.persistence.analytics.repository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import de.starwit.persistence.sae.entity.SaeCountEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * AreaOccupancy Repository class
 */
@Repository
@Transactional(readOnly = true)
public class AreaOccupancyRepository {

    @PersistenceContext(unitName = "analytics")
    EntityManager entityManager;

    // @Query(value = "SELECT COUNT(a.objectId) FROM areaoccupancy a WHERE
    // a.occupancytime = :startTime", nativeQuery = true)

    // @Query(value = "SELECT * FROM areaoccupancy a WHERE a.occupancytime =
    // :startTime and a.objectclass_id = :objectClassId", nativeQuery = true)

    EntityManager getEntityManager() {
        return entityManager;
    }

    @Transactional("analyticsTransactionManager")
    public void insert(SaeCountEntity entity) {

        String insertString = "insert into areaoccupancy(occupancytime, parkingareaid, count, objectclassid) values(:occupancytime,:parkingareaid, :count, :classId)";

        getEntityManager().createNativeQuery(insertString)
                .setParameter("occupancytime", ZonedDateTime.ofInstant(entity.getCaptureTs(), ZoneId.systemDefault()))
                .setParameter("parkingareaid", 1)
                .setParameter("count", entity.getCount())
                .setParameter("classId", entity.getObjectClassId())
                .executeUpdate();
    }

    public List<AreaOccupancyEntity> findFirst100() {
        String queryString = "select * from areaoccupancy order by occupancytime desc limit 100";
        return getEntityManager().createNativeQuery(queryString).getResultList();
    }

}
