package de.starwit.service.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import de.starwit.persistence.databackend.entity.ObservationJobEntity;
import de.starwit.persistence.databackend.entity.JobType;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.repository.SaeDao;
import de.starwit.service.analytics.AreaOccupancyService;

@ExtendWith(MockitoExtension.class)
public class AreaOccupancyJobTest {

    @Mock
    SaeDao saeDaoMock;

    @Mock
    AreaOccupancyService serviceMock;
    
    @Test
    public void testAreaOccupancy() throws InterruptedException {
        ObservationJobEntity jobEntity = prepareJobEntity();
        JobData<SaeDetectionEntity> jobData = new JobData<>(jobEntity);

        List<SaeDetectionEntity> detections = Arrays.asList(
            Helper.createDetection(Instant.ofEpochSecond(0), new Point2D.Double(50, 50)),
            Helper.createDetection(Instant.ofEpochSecond(0), new Point2D.Double(50, 50)),
            Helper.createDetection(Instant.ofEpochSecond(0), new Point2D.Double(50, 200)),
            Helper.createDetection(Instant.ofEpochSecond(0), new Point2D.Double(50, 200)),
            Helper.createDetection(Instant.ofEpochSecond(1), new Point2D.Double(50, 50)),
            Helper.createDetection(Instant.ofEpochSecond(1), new Point2D.Double(50, 50)),
            Helper.createDetection(Instant.ofEpochSecond(1), new Point2D.Double(50, 50)),
            Helper.createDetection(Instant.ofEpochSecond(1), new Point2D.Double(50, 200)),
            Helper.createDetection(Instant.ofEpochSecond(2), new Point2D.Double(50, 50)),
            Helper.createDetection(Instant.ofEpochSecond(2), new Point2D.Double(50, 50))
        );

        when(saeDaoMock.getDetectionData(any(), any(), any())).thenReturn(detections);

        AreaOccupancyJob testee = new AreaOccupancyJob(saeDaoMock, serviceMock);

        testee.run(jobData);
        
        ArgumentCaptor<ZonedDateTime> timeCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
        ArgumentCaptor<Long> countCaptor = ArgumentCaptor.forClass(Long.class);

        verify(serviceMock, times(1)).addEntry(any(), timeCaptor.capture(), countCaptor.capture());
        assertThat(timeCaptor.getValue().toEpochSecond()).isEqualTo(1);
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
