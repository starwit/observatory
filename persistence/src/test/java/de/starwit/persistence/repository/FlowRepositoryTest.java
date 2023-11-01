package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.starwit.persistence.entity.FlowEntity;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Tests for FlowRepository
 */
@DataJpaTest
public class FlowRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FlowRepository repository;

    @Test
    public void testFindAll() {
        List<FlowEntity> flows = repository.findAll();
        assertTrue(flows.isEmpty());
    }
}
