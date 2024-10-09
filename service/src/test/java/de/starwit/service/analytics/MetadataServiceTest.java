package de.starwit.service.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.starwit.persistence.analytics.entity.CoordinateEntity;
import de.starwit.persistence.analytics.entity.MetadataEntity;
import de.starwit.persistence.analytics.repository.MetadataRepository;
import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.persistence.databackend.entity.PointEntity;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class MetadataServiceTest {

    @MockBean
    MetadataRepository metadataRepository;

    @MockBean
    CoordinateService coordinateService;
    
    @InjectMocks
    MetadataService metadataService;

    static List<CoordinateEntity> coordinates = new ArrayList<>();

    @BeforeAll
    public static void init() {
        CoordinateEntity coordinate = new CoordinateEntity();
        coordinate.setLatitude(new BigDecimal(0.0));
        coordinate.setLongitude(new BigDecimal(0.0));
        coordinates.add(coordinate);
        coordinate = new CoordinateEntity();
        coordinate.setLatitude(new BigDecimal(0.0001));
        coordinate.setLongitude(new BigDecimal(0.0001));
        coordinates.add(coordinate);
    }
    
    @BeforeEach
    public void setupMocks() {
        when(metadataRepository.findFirstByNameAndClassification(any(), any())).thenReturn(null);
        when(metadataRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(coordinateService.saveCoordinatesForJob(any())).thenReturn(coordinates);
    }

    @Test
    public void testInitialization() {
        assertNotNull(metadataRepository);
        assertNotNull(coordinateService);
        assertNotNull(metadataService);
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
