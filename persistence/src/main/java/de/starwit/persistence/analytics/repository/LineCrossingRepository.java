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
        String insertString = "insert into linecrossing(crossing_time, observation_area_id, object_id, direction, object_class_id, metadata_id) values(:crossingTime, :observationAreaId, :objectId, :direction, :classId, :metadataId)";

        entityManager.createNativeQuery(insertString)
                .setParameter("crossingTime", entity.getCrossingTime())
                .setParameter("observationAreaId", entity.getObservationAreaId())
                .setParameter("objectId", entity.getObjectId())
                .setParameter("direction", entity.getDirection().toString())
                .setParameter("classId", entity.getObjectClassId())
                .setParameter("metadataId", entity.getMetadataId())
                .executeUpdate();
   }

    // TODO
    public List<LineCrossingEntity> findFirst100() {
        String queryString = "select * from linecrossing order by crossing_time desc limit 100";
        return getEntityManager().createNativeQuery(queryString).getResultList();
    }

}