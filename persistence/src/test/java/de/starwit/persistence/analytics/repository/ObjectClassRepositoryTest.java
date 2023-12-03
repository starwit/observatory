package de.starwit.persistence.analytics.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.starwit.persistence.analytics.entity.ObjectClassEntity;

/**
 * Tests for ObjectClassRepository
 */
@DataJpaTest
class ObjectClassRepositoryTest {

    @Autowired
    private ObjectClassRepository repository;

    @Test
    void testFindAll() {
        List<ObjectClassEntity> objectclasss = repository.findAll();
        assertTrue(objectclasss.isEmpty());
    }
}
