package de.starwit.persistence.analytics.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.starwit.persistence.analytics.entity.LineCrossingEntity;

/**
 * Tests for LineCrossingRepository
 */
@DataJpaTest
class LineCrossingRepositoryTest {

    @Autowired
    private LineCrossingRepository repository;

    @Test
    void testFindAll() {
        List<LineCrossingEntity> linecrossings = repository.findAll();
        assertTrue(linecrossings.isEmpty());
    }
}
