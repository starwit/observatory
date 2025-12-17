package de.starwit.service.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.geom.Point2D;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.starwit.persistence.observatory.entity.JobType;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.persistence.observatory.entity.PointEntity;
import de.starwit.service.jobs.areaoccupancy.AreaOccupancyJob;
import de.starwit.service.jobs.areaoccupancy.AreaOccupancyObservation;
import de.starwit.service.sae.SaeDetectionDto;
import de.starwit.testing.SaeDump;
import de.starwit.visionapi.Sae.SaeMessage;

@ExtendWith(MockitoExtension.class)
public class AreaOccupancyJobTest {

    @Mock
    Consumer<AreaOccupancyObservation> observationConsumerMock;

    @Test
    public void testAreaOccupancySynthetic() throws InterruptedException {
        ObservationJobEntity jobEntity = prepareJobEntity(Arrays.asList(
            Helper.createPoint(0, 0),
            Helper.createPoint(100, 0),
            Helper.createPoint(100, 100),
            Helper.createPoint(0, 100)
        ));
        
        // The dummy entries just serve to trigger the event-based processing in the job
        List<SaeDetectionDto> detections = Arrays.asList(
            Helper.createDetection(Instant.ofEpochMilli(0000), new Point2D.Double(50, 50), "dummy"),
            
            Helper.createDetection(Instant.ofEpochMilli(1000), new Point2D.Double(50, 50), "obj1"),
            Helper.createDetection(Instant.ofEpochMilli(1000), new Point2D.Double(50, 50), "obj2"),
            Helper.createDetection(Instant.ofEpochMilli(1000), new Point2D.Double(50, 200), "obj3"),
            
            Helper.createDetection(Instant.ofEpochMilli(3000), new Point2D.Double(50, 50), "obj1"),
            Helper.createDetection(Instant.ofEpochMilli(3000), new Point2D.Double(50, 50), "obj2"),
            Helper.createDetection(Instant.ofEpochMilli(3000), new Point2D.Double(50, 50), "obj3"),
            Helper.createDetection(Instant.ofEpochMilli(3000), new Point2D.Double(50, 200), "obj4"),

            Helper.createDetection(Instant.ofEpochMilli(9900), new Point2D.Double(50, 50), "obj1"),
            Helper.createDetection(Instant.ofEpochMilli(9900), new Point2D.Double(50, 50), "obj2"),

            Helper.createDetection(Instant.ofEpochMilli(10100), new Point2D.Double(50, 50), "dummy")
        );
            
        AreaOccupancyJob testee = new AreaOccupancyJob(jobEntity, Duration.ofSeconds(10), 0.001, 0.1, observationConsumerMock);

        for (SaeDetectionDto det : detections) {
            testee.processNewDetection(det);
        }

        ArgumentCaptor<AreaOccupancyObservation> observationCaptor = ArgumentCaptor.forClass(AreaOccupancyObservation.class);

        verify(observationConsumerMock, times(2)).accept(observationCaptor.capture());
        assertThat(observationCaptor.getValue().occupancyTime().toInstant().toEpochMilli()).isEqualTo(10100);
        assertThat(observationCaptor.getValue().count()).isEqualTo(2);
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

        AreaOccupancyJob testee = new AreaOccupancyJob(jobEntity, Duration.ofSeconds(10), 0.001, 0.1, observationConsumerMock);
        
        for (SaeMessage msg : saeDump) {
            for (SaeDetectionDto dto : SaeDetectionDto.from(msg)) {
                testee.processNewDetection(dto);
            }
        }

        ArgumentCaptor<AreaOccupancyObservation> observationCaptor = ArgumentCaptor.forClass(AreaOccupancyObservation.class);
        
        verify(observationConsumerMock, times(6)).accept(observationCaptor.capture());
        
        assertThat(observationCaptor.getAllValues().stream().map(o -> o.count())).containsExactly(0L, 22L, 19L, 19L, 21L, 20L);
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

}
