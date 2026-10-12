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

import de.starwit.persistence.analytics.entity.Direction;
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

    @Test
    void testDeleteByMetadataIdsFrom() {
        ZonedDateTime cutoff = ZonedDateTime.of(2026, 10, 5, 16, 0, 0, 0, ZoneOffset.UTC);
        insert(cutoff.minusSeconds(1), 1001L);
        insert(cutoff, 1001L);
        insert(cutoff.plusSeconds(1), 1001L);
        insert(cutoff.plusSeconds(1), 1002L);

        // cutoff is inclusive, other metadata ids are untouched
        assertEquals(2, repository.deleteByMetadataIdsFrom(List.of(1001L), cutoff));
        assertEquals(0, repository.deleteByMetadataIdsFrom(List.of(1001L), cutoff));
        assertEquals(1, repository.deleteByMetadataIdsFrom(List.of(1001L), cutoff.minusSeconds(1)));
        assertEquals(1, repository.deleteByMetadataIdsFrom(List.of(1002L), cutoff));
    }

    private void insert(ZonedDateTime crossingTime, Long metadataId) {
        LineCrossingEntity entity = new LineCrossingEntity();
        entity.setCrossingTime(crossingTime);
        entity.setObjectId("obj");
        entity.setDirection(Direction.in);
        entity.setObjectClassId(2L);
        entity.setMetadataId(metadataId);
        repository.insert(entity);
    }
}
