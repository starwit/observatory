package de.starwit.persistence.analytics.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.analytics.entity.LineCrossingEntity;
import de.starwit.persistence.sae.repository.SaeRepository;

/**
 * Tests for LineCrossingRepository
 */
// @DataJpaTest(includeFilters = @ComponentScan.Filter(type =
// FilterType.ANNOTATION, classes = Repository.class), excludeFilters =
// @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value =
// SaeRepository.class))
class LineCrossingRepositoryTest {

    // @Autowired
    private LineCrossingRepository repository;

    // @Test
    void testFindFirst100() {
        List<LineCrossingEntity> linecrossings = repository.findFirst100();
        assertTrue(linecrossings.isEmpty());
    }
}
