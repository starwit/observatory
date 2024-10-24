package de.starwit.service.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.geom.Point2D;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.starwit.persistence.observatory.entity.JobType;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

@ExtendWith(MockitoExtension.class)
public class AreaOccupancyJobTest {

    @Mock
    AreaOccupancyObservationListener observationListenerMock;
    
    @Test
    public void testAreaOccupancy() throws InterruptedException {
        ObservationJobEntity jobEntity = prepareJobEntity();

        List<SaeDetectionDto> detections = Arrays.asList(
            Helper.createDetection(Instant.ofEpochMilli(0000), new Point2D.Double(50, 50), "obj1"),
            Helper.createDetection(Instant.ofEpochMilli(0000), new Point2D.Double(50, 50), "obj2"),
            Helper.createDetection(Instant.ofEpochMilli(0000), new Point2D.Double(50, 200), "obj3"),
            Helper.createDetection(Instant.ofEpochMilli(2000), new Point2D.Double(50, 50), "obj1"),
            Helper.createDetection(Instant.ofEpochMilli(2000), new Point2D.Double(50, 50), "obj2"),
            Helper.createDetection(Instant.ofEpochMilli(2000), new Point2D.Double(50, 50), "obj3"),
            Helper.createDetection(Instant.ofEpochMilli(2000), new Point2D.Double(50, 200), "obj4"),
            Helper.createDetection(Instant.ofEpochMilli(5200), new Point2D.Double(50, 50), "obj1"),
            Helper.createDetection(Instant.ofEpochMilli(5200), new Point2D.Double(50, 50), "obj2")
        );

        AreaOccupancyJob testee = new AreaOccupancyJob(jobEntity, observationListenerMock);

        for (SaeDetectionDto det : detections) {
            // testee.process(det);
        }
        
        ArgumentCaptor<ZonedDateTime> timeCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
        ArgumentCaptor<Long> countCaptor = ArgumentCaptor.forClass(Long.class);

        verify(observationListenerMock, times(1)).onObservation(any(), timeCaptor.capture(), countCaptor.capture());
        assertThat(timeCaptor.getValue().toEpochSecond()).isEqualTo(2);
        assertThat(countCaptor.getValue()).isEqualTo(3);
    }

    static ObservationJobEntity prepareJobEntity() {
        ObservationJobEntity entity = new ObservationJobEntity();
        entity.setCameraId("camId");
        entity.setDetectionClassId(1);
        
        // This is a square of width 100
        entity.setGeometryPoints(Arrays.asList(
            Helper.createPoint(0, 0),
            Helper.createPoint(100, 0),
            Helper.createPoint(100, 100),
            Helper.createPoint(0, 100)
        ));
        entity.setType(JobType.AREA_OCCUPANCY);
        entity.setGeoReferenced(false);

        return entity;
    }
}
