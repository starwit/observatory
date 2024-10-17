package de.starwit.service.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.geom.Point2D;
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
import de.starwit.persistence.observatory.entity.JobType;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.sae.SaeDetectionDto;

@ExtendWith(MockitoExtension.class)
public class LineCrossingJobTest {

    @Mock
    LineCrossingObservationListener observationListenerMock;
    
    @Test
    public void testLineCrossing() throws InterruptedException {

        ObservationJobEntity entity = prepareJobEntity();
        
        // No point on trajectory should be ON the counting line (b/c direction is then ambiguous)
        List<SaeDetectionDto> detections = createLinearTrajectory(
            new Point2D.Double(50, 55), new Point2D.Double(50, 155), 
            10, Duration.ofMillis(250));

        LineCrossingJob testee = new LineCrossingJob(entity, observationListenerMock);

        for (SaeDetectionDto det : detections) {
            testee.processNewDetection(det);
        }
        
        ArgumentCaptor<Direction> directionCaptor = ArgumentCaptor.forClass(Direction.class);
        verify(observationListenerMock, times(1)).onObservation(any(), directionCaptor.capture(), any());

        assertThat(directionCaptor.getValue()).isEqualTo(Direction.out);
    }

    static ObservationJobEntity prepareJobEntity() {
        ObservationJobEntity entity = new ObservationJobEntity();
        entity.setCameraId("camId");
        entity.setDetectionClassId(1);
        entity.setGeometryPoints(Arrays.asList(
            Helper.createPoint(0, 100), 
            Helper.createPoint(100, 100)
        ));
        entity.setType(JobType.LINE_CROSSING);
        entity.setGeoReferenced(false);

        return entity;
    }

    static List<SaeDetectionDto> createLinearTrajectory(Point2D start, Point2D end, int numSteps, Duration stepInterval) {
        List<SaeDetectionDto> trajectory = new ArrayList<>();

        Instant startTime = Instant.now();

        trajectory.add(Helper.createDetection(startTime, new Point2D.Double(start.getX(), start.getY())));
        double currentX = start.getX();
        double currentY = start.getY();
        
        for (int i = 0; i < numSteps; i++) {
            currentX += (end.getX() - start.getX()) / numSteps;
            currentY += (end.getY() - start.getY()) / numSteps;
            trajectory.add(Helper.createDetection(startTime.plus(stepInterval.multipliedBy(i+1)), new Point2D.Double(currentX, currentY)));
        }

        return trajectory;
    }

}
