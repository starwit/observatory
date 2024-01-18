package de.starwit.service.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.databackend.entity.AnalyticsJobEntity;
import de.starwit.persistence.databackend.entity.JobType;
import de.starwit.persistence.databackend.entity.PointEntity;
import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.repository.SaeDao;
import de.starwit.service.analytics.LineCrossingService;

@ExtendWith(MockitoExtension.class)
public class LineCrossingJobTest {

    @Mock
    SaeDao saeDaoMock;

    @Mock
    LineCrossingService serviceMock;
    
    @Test
    public void testLineCrossing() throws InterruptedException {

        AnalyticsJobEntity entity = prepareJobEntity();
        JobData<SaeDetectionEntity> jobData = new JobData<>(entity);
        
        // No point on trajectory should be ON the counting line (b/c direction is then ambiguous)
        List<SaeDetectionEntity> detections = createLinearTrajectory(
            new Point2D.Double(50, 55), new Point2D.Double(50, 155), 
            10, Duration.ofMillis(250));
        when(saeDaoMock.getDetectionData(any(), any(), any())).thenReturn(detections);
        
        LineCrossingJob testee = new LineCrossingJob(saeDaoMock, serviceMock);

        testee.run(jobData);
        
        ArgumentCaptor<Direction> directionCaptor = ArgumentCaptor.forClass(Direction.class);
        verify(serviceMock, times(1)).addEntry(any(), any(), directionCaptor.capture(), any());

        assertThat(directionCaptor.getValue()).isEqualTo(Direction.out);
    }

    static AnalyticsJobEntity prepareJobEntity() {
        PointEntity pointEntity1 = new PointEntity();
        pointEntity1.setX(new BigDecimal(0.0));
        pointEntity1.setY(new BigDecimal(100.0));
        PointEntity pointEntity2 = new PointEntity();
        pointEntity2.setX(new BigDecimal(100.0));
        pointEntity2.setY(new BigDecimal(100.0));

        AnalyticsJobEntity entity = new AnalyticsJobEntity();
        entity.setCameraId("camId");
        entity.setDetectionClassId(1);
        entity.setGeometryPoints(Arrays.asList(pointEntity1, pointEntity2));
        entity.setType(JobType.LINE_CROSSING);

        return entity;
    }

    static List<SaeDetectionEntity> createLinearTrajectory(Point2D start, Point2D end, int numSteps, Duration stepInterval) {
        List<SaeDetectionEntity> trajectory = new ArrayList<>();

        Instant startTime = Instant.now().minusSeconds(100);

        trajectory.add(createDetection(startTime, new Point2D.Double(start.getX(), start.getY())));
        double currentX = start.getX();
        double currentY = start.getY();
        
        for (int i = 0; i < numSteps; i++) {
            currentX += (end.getX() - start.getX()) / numSteps;
            currentY += (end.getY() - start.getY()) / numSteps;
            trajectory.add(createDetection(startTime.plus(stepInterval.multipliedBy(i+1)), new Point2D.Double(currentX, currentY)));
        }

        return trajectory;
    }

    static SaeDetectionEntity createDetection(Instant captureTs, Point2D center) {
        SaeDetectionEntity det = new SaeDetectionEntity();

        det.setCaptureTs(captureTs);
        det.setMinX(center.getX() - 50);
        det.setMaxX(center.getX() + 50);
        det.setMinY(center.getY() - 50);
        det.setMaxY(center.getY() + 50);

        // defaults (we do not need to vary these)
        det.setCameraId("camId");
        det.setClassId(1);
        det.setObjectId("objId");
        det.setConfidence(0.5);
        
        return det;
    }

}
