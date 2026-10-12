package de.starwit.persistence.analytics.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;

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
        AreaOccupancyEntity entity = new AreaOccupancyEntity();
        entity.setOccupancyTime(ZonedDateTime.now());
        entity.setCount(4);
        entity.setObjectClassId(2);
        entity.setMetadataId(1L);
        repository.insert(entity);

        List<AreaOccupancyEntity> areaoccupancys = repository.findFirst100();
        assertTrue(areaoccupancys.size() > 0);
    }

    @Test
    void testDeleteByMetadataIdsAndObjectClassIdFrom() {
        ZonedDateTime cutoff = ZonedDateTime.of(2026, 10, 5, 16, 0, 0, 0, ZoneOffset.UTC);
        insert(cutoff.minusSeconds(1), 1001L, 2);
        insert(cutoff, 1001L, 2);
        insert(cutoff.plusSeconds(1), 1001L, 2);
        insert(cutoff.plusSeconds(1), 1001L, 3);
        insert(cutoff.plusSeconds(1), 1002L, 2);

        // cutoff is inclusive, other metadata ids and object classes are untouched
        assertEquals(2, repository.deleteByMetadataIdsAndObjectClassIdFrom(List.of(1001L), 2, cutoff));
        assertEquals(0, repository.deleteByMetadataIdsAndObjectClassIdFrom(List.of(1001L), 2, cutoff));
        assertEquals(1, repository.deleteByMetadataIdsAndObjectClassIdFrom(List.of(1001L), 2, cutoff.minusSeconds(1)));
        assertEquals(1, repository.deleteByMetadataIdsAndObjectClassIdFrom(List.of(1001L), 3, cutoff));
        assertEquals(1, repository.deleteByMetadataIdsAndObjectClassIdFrom(List.of(1002L), 2, cutoff));
    }

    private void insert(ZonedDateTime occupancyTime, Long metadataId, Integer objectClassId) {
        AreaOccupancyEntity entity = new AreaOccupancyEntity();
        entity.setOccupancyTime(occupancyTime);
        entity.setCount(1);
        entity.setObjectClassId(objectClassId);
        entity.setMetadataId(metadataId);
        repository.insert(entity);
    }
}
