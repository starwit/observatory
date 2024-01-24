package de.starwit.persistence.analytics.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import de.starwit.persistence.analytics.entity.LineCrossingEntity;

/**
 * Tests for LineCrossingRepository
 */
@EnableAutoConfiguration
@SpringBootTest
class LineCrossingRepositoryTest {

    @Autowired
    private LineCrossingRepository repository;

    @Test
    void testFindFirst100() {
        List<LineCrossingEntity> linecrossings = repository.findFirst100();
        assertTrue(linecrossings.isEmpty());
    }
}
