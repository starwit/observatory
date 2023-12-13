package de.starwit.persistence.databackend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;

/**
 * Tests for FlowRepository
 */
@EnableAutoConfiguration
@SpringBootTest
class AnalyticsJobRepositoryTest {

    @Autowired
    private AnalyticsJobRepository repository;

    @Test
    void testFindAll() {
        List<AnalyticsJobEntity> flows = repository.findAll();
        assertTrue(flows.size() > 0);
    }
}
