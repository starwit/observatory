package de.starwit.persistence.databackend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.starwit.persistence.databackend.entity.PointEntity;

/**
 * Tests for ObjectClassRepository
 */
@DataJpaTest
class PointRepositoryTest {

    @Autowired
    private PointRepository repository;

    @Test
    void testFindAll() {
        List<PointEntity> objectclasss = repository.findAll();
        assertTrue(objectclasss.isEmpty());
    }
}
