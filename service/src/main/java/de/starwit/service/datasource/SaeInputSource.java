package de.starwit.service.datasource;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SaeInputSource {

    @Value("${saeInput.detectionsTableName}")
    private String detectionsTableName;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    private ZonedDateTime lastRetrievedTime;

    public SaeInputSource() {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @SuppressWarnings("unchecked")
    private List<SaeDetectionDTO> getDetectionsSince(ZonedDateTime since, String cameraId, Integer detectionClass) {
        Query query = this.entityManager.createNativeQuery("""
            SELECT * 
            FROM :tableName t 
            WHERE t.CAPTURE_TS > :since AND t.CAMERA_ID = :cameraId AND t.CLASS_ID = :detectionClass
            ORDER BY t.CAPTURE_TS ASC
            """, "SaeDetectionDTO");
        query.setParameter("tableName", detectionsTableName);
        query.setParameter("since", since);
        query.setParameter("cameraId", cameraId);
        query.setParameter("detectionClass", detectionClass);
        return query.getResultList();
    }

}
