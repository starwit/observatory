package de.starwit.persistence.databackend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;

/**
 * Tests for FlowRepository
 */
@DataJpaTest
class AnalyticsJobRepositoryTest {

    @Autowired
    private AnalyticsJobRepository repository;

    @Test
    void testFindAll() {
        List<AnalyticsJobEntity> flows = repository.findAll();
        assertTrue(flows.isEmpty());
    }
}
