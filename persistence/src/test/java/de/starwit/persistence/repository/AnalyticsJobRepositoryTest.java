package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import de.starwit.persistence.databackendconfig.entity.AnalyticsJobEntity;
import de.starwit.persistence.databackendconfig.repository.AnalyticsJobRepository;

/**
 * Tests for FlowRepository
 */
@DataJpaTest
public class AnalyticsJobRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AnalyticsJobRepository repository;

    @Test
    public void testFindAll() {
        List<AnalyticsJobEntity> flows = repository.findAll();
        assertTrue(flows.isEmpty());
    }
}
