package de.starwit.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.starwit.persistence.entity.ObjectClassEntity;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Tests for ObjectClassRepository
 */
@DataJpaTest
public class ObjectClassRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObjectClassRepository repository;

    @Test
    public void testFindAll() {
        List<ObjectClassEntity> objectclasss = repository.findAll();
        assertTrue(objectclasss.isEmpty());
    }
}
