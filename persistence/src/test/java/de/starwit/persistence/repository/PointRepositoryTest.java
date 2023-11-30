package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import de.starwit.persistence.databackendconfig.entity.PointEntity;
import de.starwit.persistence.databackendconfig.repository.PointRepository;

/**
 * Tests for ObjectClassRepository
 */
@DataJpaTest
public class PointRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PointRepository repository;

    @Test
    public void testFindAll() {
        List<PointEntity> objectclasss = repository.findAll();
        assertTrue(objectclasss.isEmpty());
    }
}
