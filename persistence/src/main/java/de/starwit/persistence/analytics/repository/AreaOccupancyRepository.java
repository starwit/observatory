package de.starwit.persistence.analytics.repository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

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

    EntityManager getEntityManager() {
        return entityManager;
    }

    @Transactional("analyticsTransactionManager")
    public void insert(AreaOccupancyEntity entity) {

        String insertString = "insert into areaoccupancy(occupancytime, parkingareaid, count, objectclassid, metadataid) values(:occupancytime,:parkingareaid, :count, :classId, :metadataId)";

        getEntityManager().createNativeQuery(insertString)
                .setParameter("occupancytime", entity.getOccupancyTime())
                .setParameter("parkingareaid", 1)
                .setParameter("count", entity.getCount())
                .setParameter("classId", entity.getObjectClassId())
                .setParameter("metadataId", entity.getMetadataId())
                .executeUpdate();
    }

    public List<AreaOccupancyEntity> findFirst100() {
        String queryString = "select * from areaoccupancy order by occupancytime desc limit 100";
        return getEntityManager().createNativeQuery(queryString).getResultList();
    }

}
