package de.starwit.persistence.sae.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.sae.entity.SaeCountEntity;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class SaeRepository {

    @PersistenceContext(unitName = "sae")
    EntityManager em;

    @Value("${sae.detection.tablename}")
    private String hyperTableName;

    public List<SaeCountEntity> getDetectionData(Instant lastRetrievedTime, String cameraId,
            Integer detectionClassId) {

        String getDetectionData = ""
                + "select capture_ts, class_id, count(object_id)"
                + " from " + hyperTableName
                + " where capture_ts > :capturets"
                + " and camera_id = :cameraid"
                + " and class_id = :classid"
                + " group by capture_ts, class_id";

        Query q = em.createNativeQuery(getDetectionData, SaeCountEntity.class);
        q.setParameter("capturets", lastRetrievedTime);
        q.setParameter("cameraid", cameraId);
        q.setParameter("classid", detectionClassId);
        return q.getResultList();
    }

    public List<SaeDetectionEntity> getLineCrossingDetectionData(Instant lastRetrievedTime, String cameraId,
            Integer detectionClassId) {

        String getDetectionData = ""
                + "select * "
                + " from " + hyperTableName
                + " where capture_ts > :capturets"
                + " and camera_id = :cameraid"
                + " and class_id = :classid";

        Query q = em.createNativeQuery(getDetectionData, SaeDetectionEntity.class);
        q.setParameter("capturets", lastRetrievedTime);
        q.setParameter("cameraid", cameraId);
        q.setParameter("classid", detectionClassId);
        List<SaeDetectionEntity> result = q.getResultList();
        return result;
    }

}
