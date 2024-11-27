package de.starwit.service.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.geom.Point2D;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.test.util.ReflectionTestUtils;

import de.starwit.persistence.observatory.entity.JobType;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.persistence.observatory.entity.PointEntity;
import de.starwit.service.analytics.AreaOccupancyService;
import de.starwit.service.geojson.GeoJsonService;
import de.starwit.service.observatory.ObservationJobService;
import de.starwit.service.sae.SaeDetectionDto;
import de.starwit.testing.SaeDump;
import de.starwit.visionapi.Sae.SaeMessage;

@ExtendWith(MockitoExtension.class)
public class AreaOccupancyRunnerTest {

    @Mock
    AreaOccupancyService areaOccupancyServiceMock;

    @Mock
    ObservationJobService observationJobServiceMock;

    @Mock
    GeoJsonService geoJsonServiceMock;

    @Mock
    StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamListenerContainerMock;

    @Mock
    ScheduledExecutorService scheduledExecutorServiceMock;

    @Test
    public void testAreaOccupancySynthetic() throws InterruptedException {
        ObservationJobEntity jobEntity = prepareJobEntity(Arrays.asList(
            Helper.createPoint(0, 0),
            Helper.createPoint(100, 0),
            Helper.createPoint(100, 100),
            Helper.createPoint(0, 100)
        ));

        AreaOccupancyJob job = new AreaOccupancyJob(jobEntity, Duration.ofSeconds(10));

        List<SaeDetectionDto> detections = Arrays.asList(
            Helper.createDetection(Instant.ofEpochMilli(0000), new Point2D.Double(50, 50), "obj1"),
            Helper.createDetection(Instant.ofEpochMilli(0000), new Point2D.Double(50, 50), "obj2"),
            Helper.createDetection(Instant.ofEpochMilli(0000), new Point2D.Double(50, 200), "obj3"),
            Helper.createDetection(Instant.ofEpochMilli(2000), new Point2D.Double(50, 50), "obj1"),
            Helper.createDetection(Instant.ofEpochMilli(2000), new Point2D.Double(50, 50), "obj2"),
            Helper.createDetection(Instant.ofEpochMilli(2000), new Point2D.Double(50, 50), "obj3"),
            Helper.createDetection(Instant.ofEpochMilli(2000), new Point2D.Double(50, 200), "obj4"),
            Helper.createDetection(Instant.ofEpochMilli(10200), new Point2D.Double(50, 50), "obj1"),
            Helper.createDetection(Instant.ofEpochMilli(10200), new Point2D.Double(50, 50), "obj2")
        );

        AreaOccupancyRunner testee = prepareTestee();

        for (SaeDetectionDto det : detections) {
            testee.addDetection(job, det);
        }

        testee.runJob(job);

        ArgumentCaptor<ZonedDateTime> timeCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
        ArgumentCaptor<Long> countCaptor = ArgumentCaptor.forClass(Long.class);

        verify(areaOccupancyServiceMock, times(1)).addEntry(any(), timeCaptor.capture(), countCaptor.capture());
        assertThat(timeCaptor.getValue().toEpochSecond()).isEqualTo(10);
        assertThat(countCaptor.getValue()).isEqualTo(2);
    }

    @Test
    public void testAreaOccupancyDump() {
        SaeDump saeDump = new SaeDump(Paths.get("src/test/resources/test.saedump"));
        
        // The entire frame
        ObservationJobEntity jobEntity = prepareJobEntity(Arrays.asList(
            Helper.createPoint(0, 0),
            Helper.createPoint(1, 0),
            Helper.createPoint(1, 1),
            Helper.createPoint(0, 1)
        ));

        AreaOccupancyJob job = new AreaOccupancyJob(jobEntity, Duration.ofSeconds(10));
        
        AreaOccupancyRunner testee = prepareTestee();
        
        for (SaeMessage msg : saeDump) {
            for (SaeDetectionDto dto : SaeDetectionDto.from(msg)) {
                testee.addDetection(job, dto);
            }
        }

        testee.runJob(job);
        
        ArgumentCaptor<Long> countCaptor = ArgumentCaptor.forClass(Long.class);
        
        verify(areaOccupancyServiceMock, times(1)).addEntry(any(), any(), countCaptor.capture());
        
        assertThat(countCaptor.getValue()).isEqualTo(20);
    }

    static ObservationJobEntity prepareJobEntity(List<PointEntity> geometryPoints) {
        ObservationJobEntity entity = new ObservationJobEntity();
        entity.setCameraId("camId");
        entity.setDetectionClassId(1);
        
        entity.setGeometryPoints(geometryPoints);
        entity.setType(JobType.AREA_OCCUPANCY);
        entity.setGeoReferenced(false);

        return entity;
    }

    private AreaOccupancyRunner prepareTestee() {
        AreaOccupancyRunner testee = new AreaOccupancyRunner(InstantSource.fixed(Instant.ofEpochMilli(0)), scheduledExecutorServiceMock);
        ReflectionTestUtils.setField(testee, "ANALYZING_INTERVAL", Duration.ofSeconds(10));
        ReflectionTestUtils.setField(testee, "GEO_DISTANCE_P95_THRESHOLD", 0.001);
        ReflectionTestUtils.setField(testee, "areaOccupancyService", areaOccupancyServiceMock);
        ReflectionTestUtils.setField(testee, "geoJsonService", geoJsonServiceMock);
        return testee;
    }
}
