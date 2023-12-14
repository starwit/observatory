package de.starwit.persistence.analytics.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import de.starwit.persistence.analytics.entity.ObjectClassEntity;

/**
 * Tests for ObjectClassRepository
 */
@EnableAutoConfiguration
@SpringBootTest
class ObjectClassRepositoryTest {

    @Autowired
    private ObjectClassRepository repository;

    @Test
    void testFindAll() {
        List<ObjectClassEntity> objectclasses = repository.findAll();
        assertEquals(1, objectclasses.size());
    }
}
