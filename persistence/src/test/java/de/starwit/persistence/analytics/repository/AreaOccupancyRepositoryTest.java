package de.starwit.persistence.analytics.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import de.starwit.persistence.sae.entity.SaeCountEntity;
import de.starwit.persistence.sae.repository.SaeRepository;

/**
 * Tests for AreaOccupancyRepository
 */

// @DataJpaTest(includeFilters = @ComponentScan.Filter(type =
// FilterType.ANNOTATION, classes = Repository.class), excludeFilters =
// @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value =
// SaeRepository.class))
class AreaOccupancyRepositoryTest {

    // @Autowired
    private AreaOccupancyRepository areaOccupancyRepository;

    // @Test
    void testFindFirst100() {
        List<AreaOccupancyEntity> areaoccupancys = areaOccupancyRepository.findFirst100();
        assertTrue(areaoccupancys.isEmpty());
    }

    // @Test
    void testSave() {
        SaeCountEntity entity = new SaeCountEntity();
        entity.setCaptureTs(Instant.now());
        entity.setCount(4);
        entity.setObjectClassId(2);
        areaOccupancyRepository.insert(entity);

        List<AreaOccupancyEntity> areaoccupancys = areaOccupancyRepository.findFirst100();
        assertTrue(areaoccupancys.size() > 0);
    }
}
