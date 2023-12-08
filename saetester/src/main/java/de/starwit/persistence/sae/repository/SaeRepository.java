package de.starwit.persistence.sae.repository;

import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Component
@Repository
public class SaeRepository {

    @PersistenceContext(unitName = "sae")
    EntityManager em;

    private String INSERT_QUERY = "INSERT INTO detection(capture_ts, class_id, confidence, object_id, min_x, min_y, max_x, max_y, camera_id) VALUES (?,?,?,?,?,?,?,?,?)";

    @Transactional
    public void insertDetectionData(UUID uuid, Timestamp captureTimestamp) {
        Query q = em.createNativeQuery(INSERT_QUERY);
        q.setParameter(1, captureTimestamp);
        q.setParameter(2, 2);
        q.setParameter(3, 0.0f);
        q.setParameter(4, uuid.toString());
        q.setParameter(5, 0);
        q.setParameter(6, 1);
        q.setParameter(7, 0);
        q.setParameter(8, 1);
        q.setParameter(9, "RangelineSMedicalDr");
        q.executeUpdate();
    }

}
