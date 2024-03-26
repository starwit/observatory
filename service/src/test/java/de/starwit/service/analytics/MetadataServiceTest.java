package de.starwit.service.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import de.starwit.persistence.analytics.entity.MetadataEntity;
import de.starwit.persistence.analytics.repository.CoordinateRepository;
import de.starwit.persistence.analytics.repository.MetadataRepository;
import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.persistence.databackend.entity.PointEntity;

@EnableAutoConfiguration
@SpringBootTest
public class MetadataServiceTest {

    @MockBean
    MetadataRepository metadataRepository;

    @MockBean
    CoordinateRepository coordinateRepository;
    
    @Autowired
    MetadataService metadataService;

    @BeforeEach
    public void setupMocks() {
        when(metadataRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(coordinateRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    public void testGetMetadataForGeoJob() {
        ObservationJobEntity jobEntity = createJobEntity(1, true);
        MetadataEntity metadataEntity = metadataService.saveMetadataForJob(jobEntity);
        assertThat(metadataEntity.getGeoReferenced()).isTrue();
        assertThat(metadataEntity.getGeometryCoordinates()).hasSize(2);
    }

    @Test
    public void testGetMetadataForNonGeoJob() {
        ObservationJobEntity jobEntity = createJobEntity(1, false);
        MetadataEntity metadataEntity = metadataService.saveMetadataForJob(jobEntity);
        assertThat(metadataEntity.getGeoReferenced()).isFalse();
        assertThat(metadataEntity.getGeometryCoordinates()).hasSize(0);
    }

    private static ObservationJobEntity createJobEntity(int numValue, boolean geo) {
        List<PointEntity> points = Arrays.asList(
            createPoint(1),
            createPoint(2)
        );
        ObservationJobEntity jobEntity = new ObservationJobEntity();
        jobEntity.setName("job" + numValue);
        jobEntity.setGeoReferenced(geo);
        jobEntity.setGeometryPoints(points);
        return jobEntity;
    }

    private static PointEntity createPoint(int numValue) {
        PointEntity point = new PointEntity();
        point.setOrderIdx(numValue);
        point.setX(BigDecimal.valueOf(numValue));
        point.setY(BigDecimal.valueOf(numValue));
        point.setLatitude(BigDecimal.valueOf(numValue));
        point.setLongitude(BigDecimal.valueOf(numValue));
        return point;
    }
}
