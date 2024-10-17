package de.starwit.persistence.ovservatory.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import de.starwit.persistence.observatory.entity.PointEntity;
import de.starwit.persistence.observatory.repository.PointRepository;

/**
 * Tests for ObjectClassRepository
 */
@EnableAutoConfiguration
@SpringBootTest
class PointRepositoryTest {

    @Autowired
    private PointRepository repository;

    @Test
    void testFindAll() {
        List<PointEntity> objectclasss = repository.findAll();
        assertTrue(objectclasss.isEmpty());
    }
}
