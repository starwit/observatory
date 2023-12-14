package de.starwit.persistence.analytics.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import de.starwit.persistence.sae.entity.SaeCountEntity;

/**
 * Tests for AreaOccupancyRepository
 */
@EnableAutoConfiguration
@SpringBootTest
class AreaOccupancyRepositoryTest {

    @Autowired
    private AreaOccupancyRepository repository;

    void testFindFirst100() {
        List<AreaOccupancyEntity> areaoccupancys = repository.findFirst100();
        assertTrue(areaoccupancys.isEmpty());
    }

    @Test
    void testSave() {
        SaeCountEntity entity = new SaeCountEntity();
        entity.setCaptureTs(Instant.now());
        entity.setCount(4);
        entity.setObjectClassId(2);
        repository.insert(entity);

        List<AreaOccupancyEntity> areaoccupancys = repository.findFirst100();
        assertTrue(areaoccupancys.size() > 0);
    }
}
