package de.starwit.persistence.sae.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class SaeRepository {

    @PersistenceContext(unitName = "sae")
    EntityManager em;

    private static String getDetectionData = "select * from detection2 where capture_ts > :capturets and camera_id = :cameraid and class_id = :classid order by capture_ts ASC";

    public List<SaeDetectionEntity> getDetectionData(Instant lastRetrievedTime, String cameraId,
            Integer detectionClassId) {
        Query q = em.createNativeQuery(SaeRepository.getDetectionData, SaeDetectionEntity.class);
        q.setParameter("capturets", lastRetrievedTime);
        q.setParameter("cameraid", cameraId);
        q.setParameter("classid", detectionClassId);
        return q.getResultList();
    }

}
