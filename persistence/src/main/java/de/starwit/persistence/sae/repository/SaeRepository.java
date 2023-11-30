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

    @PersistenceContext(unitName = "saeEntityManagerFactory")
    EntityManager em;

    private static String getDetectionData = "select * from detection2 where \"CAPTURE_TS\" > :capturets and \"CAMERA_ID\" = :cameraid and \"CLASS_ID\" = :classid order by \"CAPTURE_TS\" ASC";

    public List<SaeDetectionEntity> getDetectionData(Instant lastRetrievedTime, String cameraId,
            Integer detectionClassId) {
        Query q = em.createNativeQuery(getDetectionData, SaeDetectionEntity.class);
        q.setParameter("capturets", lastRetrievedTime);
        q.setParameter("cameraid", cameraId);
        q.setParameter("classid", detectionClassId);
        List<SaeDetectionEntity> test = q.getResultList();
        return test;
    }

}
