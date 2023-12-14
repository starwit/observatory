package de.starwit.persistence.analytics.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import de.starwit.persistence.analytics.entity.LineCrossingEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional(readOnly = true)
public class LineCrossingRepository {

    @PersistenceContext(unitName = "analytics")
    EntityManager entityManager;

    EntityManager getEntityManager() {
        return entityManager;
    }

    @Transactional("analyticsTransactionManager")
    public void insert(LineCrossingEntity entity) {
        String insertString = "insert into linecrossing(crossingtime, parkingareaid, objectid, direction, objectclassid) values(:crossingtime, :parkingareaid, :objectid, :direction, :classId)";

        entityManager.createNativeQuery(insertString)
                .setParameter("crossingtime", entity.getCrossingTime())
                .setParameter("parkingareaid", entity.getParkingAreaId())
                .setParameter("objectid", entity.getObjectId())
                .setParameter("direction", entity.getDirection().toString())
                .setParameter("classId", entity.getObjectClassId())
                .executeUpdate();
   }

    // TODO
    public List<LineCrossingEntity> findFirst100() {
        String queryString = "select * from linecrossing order by crossingtime desc limit 100";
        return getEntityManager().createNativeQuery(queryString).getResultList();
    }

}