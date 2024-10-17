package de.starwit.persistence.ovservatory.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.persistence.observatory.repository.ObservationJobRepository;

/**
 * Tests for FlowRepository
 */
@EnableAutoConfiguration
@SpringBootTest
class ObservationJobRepositoryTest {

    @Autowired
    private ObservationJobRepository repository;

    @Test
    void testFindAll() {
        List<ObservationJobEntity> flows = repository.findAll();
        assertTrue(flows.isEmpty());
    }
}
