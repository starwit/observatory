package de.starwit.service.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import de.starwit.persistence.analytics.entity.AreaOccupancyEntity;
import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.analytics.entity.MetadataEntity;
import de.starwit.persistence.analytics.repository.AreaOccupancyRepository;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class AreaOccupancyServiceTest {

    @MockitoBean
    AreaOccupancyRepository areaoccupancyRepository;

    @MockitoBean
    MetadataService metadataService;

    @InjectMocks
    AreaOccupancyService areaOccupancyService;

    static final ZonedDateTime TIME = Instant.parse("2026-06-16T00:00:00Z").atZone(ZoneOffset.UTC);

    @BeforeEach
    public void setupMocks() {
        ReflectionTestUtils.setField(areaOccupancyService, "FLOW_IDLE_DECREASE_ENABLED", true);
        ReflectionTestUtils.setField(areaOccupancyService, "FLOW_IDLE_DECREASE_THRESHOLD", Duration.ofHours(1));
        ReflectionTestUtils.setField(areaOccupancyService, "FLOW_IDLE_DECREASE_INTERVAL", Duration.ofMinutes(15));
        ReflectionTestUtils.setField(areaOccupancyService, "FLOW_IDLE_DECREASE_FACTOR", 1);

        MetadataEntity metadata = new MetadataEntity();
        metadata.setId(1L);
        when(metadataService.saveMetadataForJob(any())).thenReturn(metadata);
    }

    @Test
    public void testNoDecayOnFirstEntry() {
        when(areaoccupancyRepository.findFirstByMetadataIdAndObjectClassIdOrderByOccupancytime(any(), any()))
                .thenReturn(null);

        areaOccupancyService.updateCountFromFlow(jobEntity(null), TIME, Direction.in);

        assertThat(captureInsertedCount()).isEqualTo(1);
    }

    @Test
    public void testShortGapNoDecay() {
        when(areaoccupancyRepository.findFirstByMetadataIdAndObjectClassIdOrderByOccupancytime(any(), any()))
                .thenReturn(lastEntry(5, TIME));

        areaOccupancyService.updateCountFromFlow(jobEntity(null), TIME.plusMinutes(30), Direction.in);

        assertThat(captureInsertedCount()).isEqualTo(6);
    }

    @Test
    public void testLongGapAppliesExactStepDecay() {
        when(areaoccupancyRepository.findFirstByMetadataIdAndObjectClassIdOrderByOccupancytime(any(), any()))
                .thenReturn(lastEntry(10, TIME));

        areaOccupancyService.updateCountFromFlow(jobEntity(null), TIME.plusHours(2), Direction.out);

        // delta first: 10 - 1 = 9; decay: 7200s / 900s = 8 steps -> max(0, 9 - 8) = 1
        assertThat(captureInsertedCount()).isEqualTo(1);
    }

    @Test
    public void testDecayClampsAtZero() {
        when(areaoccupancyRepository.findFirstByMetadataIdAndObjectClassIdOrderByOccupancytime(any(), any()))
                .thenReturn(lastEntry(2, TIME));

        areaOccupancyService.updateCountFromFlow(jobEntity(null), TIME.plusHours(3), Direction.out);

        // delta first: 2 - 1 = 1; decay: 10800s / 900s = 12 steps -> max(0, 1 - 12) = 0
        assertThat(captureInsertedCount()).isEqualTo(0);
    }

    @Test
    public void testGapExactlyAtThresholdNoDecay() {
        when(areaoccupancyRepository.findFirstByMetadataIdAndObjectClassIdOrderByOccupancytime(any(), any()))
                .thenReturn(lastEntry(5, TIME));

        areaOccupancyService.updateCountFromFlow(jobEntity(null), TIME.plusHours(1), Direction.in);

        assertThat(captureInsertedCount()).isEqualTo(6);
    }

    @Test
    public void testNoDecayWhenDisabled() {
        ReflectionTestUtils.setField(areaOccupancyService, "FLOW_IDLE_DECREASE_ENABLED", false);
        when(areaoccupancyRepository.findFirstByMetadataIdAndObjectClassIdOrderByOccupancytime(any(), any()))
                .thenReturn(lastEntry(10, TIME));

        areaOccupancyService.updateCountFromFlow(jobEntity(null), TIME.plusHours(2), Direction.out);

        // delta only: 10 - 1 = 9; decay would otherwise apply since elapsed (2h) exceeds threshold (1h)
        assertThat(captureInsertedCount()).isEqualTo(9);
    }

    private int captureInsertedCount() {
        ArgumentCaptor<AreaOccupancyEntity> captor = ArgumentCaptor.forClass(AreaOccupancyEntity.class);
        org.mockito.Mockito.verify(areaoccupancyRepository).insert(captor.capture());
        return captor.getValue().getCount();
    }

    private static AreaOccupancyEntity lastEntry(int count, ZonedDateTime time) {
        AreaOccupancyEntity entity = new AreaOccupancyEntity();
        entity.setCount(count);
        entity.setOccupancyTime(time);
        entity.setObjectClassId(1);
        entity.setMetadataId(1L);
        return entity;
    }

    private static ObservationJobEntity jobEntity(Integer maxCount) {
        ObservationJobEntity entity = new ObservationJobEntity();
        entity.setName("job1");
        entity.setDetectionClassId(1);
        entity.setObservationAreaId(1L);
        entity.setMaxCount(maxCount);
        return entity;
    }
}
