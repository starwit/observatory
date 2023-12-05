package de.starwit.persistence.analytics.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import de.starwit.persistence.analytics.entity.ObjectClassEntity;

/**
 * Tests for AreaOccupancyRepository
 */
@DataJpaTest
class AreaOccupancyRepositoryTest {

    @Autowired
    private AreaOccupancyRepository repository;

    @Autowired
    private ObjectClassRepository objectClassRepository;

    @Test
    void testFindAll() {
        List<AreaOccupancyEntity> areaoccupancys = repository.findAll();
        assertTrue(areaoccupancys.isEmpty());
    }

    @Test
    void testSave() {
        AreaOccupancyEntity entity = new AreaOccupancyEntity();
        entity.setOccupancyTime(ZonedDateTime.now());
        entity.setCount(4);
        ObjectClassEntity objectClassEntity = new ObjectClassEntity();
        objectClassEntity.setClassId(2);
        objectClassEntity.setName("car");
        objectClassEntity = objectClassRepository.save(objectClassEntity);
        entity.setObjectClass(objectClassEntity);
        repository.save(entity);

        List<AreaOccupancyEntity> areaoccupancys = repository.findAll();
        assertTrue(areaoccupancys.size() > 0);
    }
}
