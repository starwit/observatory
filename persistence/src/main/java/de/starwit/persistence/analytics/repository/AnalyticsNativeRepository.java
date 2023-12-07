package de.starwit.persistence.analytics.repository;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class AnalyticsNativeRepository {

    @PersistenceContext(unitName = "analytics")
    EntityManager em;

    public static String insertString = "insert into areaoccupancy(occupancytime, count) values(:occupancytime,:count)";

    @Transactional("analyticsTransactionManager")
    public void insertAreaOccupancy() {

        em.createNativeQuery(insertString)
                .setParameter("occupancytime", ZonedDateTime.now())
                .setParameter("count", 4)
                .executeUpdate();
    }

}
