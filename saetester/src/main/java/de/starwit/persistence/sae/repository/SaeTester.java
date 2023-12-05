package de.starwit.persistence.sae.repository;

import java.sql.Timestamp;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class SaeTester {

    @PersistenceContext(unitName = "sae")
    EntityManager em;

    Logger log = LoggerFactory.getLogger(this.getClass());

    private String INSERT_QUERY = "INSERT INTO detection(capture_ts, class_id, confidence, object_id, min_x, min_y, max_x, max_y, camera_id) VALUES (?,?,?,?,?,?,?,?,?)";

    @Scheduled(initialDelay = 0, fixedRate = 10)
    @Transactional
    public void insertDetectionData() {
        Timestamp captureTimestamp = new Timestamp(System.currentTimeMillis());

        Query q = em.createNativeQuery(INSERT_QUERY);
        q.setParameter(1, captureTimestamp);
        q.setParameter(2, 2);
        q.setParameter(3, 0.0f);
        q.setParameter(4, "unknown");
        q.setParameter(5, 0);
        q.setParameter(6, 1);
        q.setParameter(7, 0);
        q.setParameter(8, 1);
        q.setParameter(9, "RangelineSMedicalDr");
        q.executeUpdate();
        log.info("Added Entry to database");
    }

}
