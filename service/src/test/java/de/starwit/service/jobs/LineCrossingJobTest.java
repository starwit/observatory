package de.starwit.service.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.geom.Point2D;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.starwit.persistence.analytics.entity.Direction;
import de.starwit.persistence.observatory.entity.JobType;
import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.persistence.observatory.entity.PointEntity;
import de.starwit.service.jobs.linecrossing.LineCrossingJob;
import de.starwit.service.jobs.linecrossing.LineCrossingObservation;
import de.starwit.service.sae.SaeMessageDto;
import de.starwit.testing.SaeDump;
import de.starwit.visionapi.Sae.SaeMessage;

@ExtendWith(MockitoExtension.class)
public class LineCrossingJobTest {

    @Mock
    Consumer<LineCrossingObservation> consumerMock;

    @Test
    public void testLineCrossingSynthetic() throws InterruptedException {

        ObservationJobEntity jobEntity = prepareJobEntity(Arrays.asList(
            Helper.createPoint(0, 100), 
            Helper.createPoint(100, 100)
        ));

        // No point on trajectory should be ON the counting line (b/c direction is then ambiguous)
        List<SaeMessageDto> messages = createLinearTrajectory(
            new Point2D.Double(50, 55), new Point2D.Double(50, 155), 
            10, Duration.ofMillis(250));

        LineCrossingJob testee = new LineCrossingJob(jobEntity, Duration.ofSeconds(1), consumerMock);

        for (SaeMessageDto msg : messages) {
            testee.processNewMessage(msg);
        }
        
        ArgumentCaptor<LineCrossingObservation> observationCaptor = ArgumentCaptor.forClass(LineCrossingObservation.class);
        verify(consumerMock, times(1)).accept(observationCaptor.capture());
        
        assertThat(observationCaptor.getValue().direction()).isEqualTo(Direction.out);
    }
    
    @Test
    public void testLineCrossingDump() {
        ObservationJobEntity jobEntity = prepareJobEntity(Arrays.asList(
            Helper.createPoint(0.35, 0.5), 
            Helper.createPoint(0.7, 0.7)
        ));
        
        SaeDump saeDump = new SaeDump(Paths.get("src/test/resources/test.saedump"));

        LineCrossingJob testee = new LineCrossingJob(jobEntity, Duration.ofSeconds(1), consumerMock);
        
        for (SaeMessage msg : saeDump) {
            testee.processNewMessage(SaeMessageDto.from(msg));
        }

        ArgumentCaptor<LineCrossingObservation> observationCaptor = ArgumentCaptor.forClass(LineCrossingObservation.class);

        verify(consumerMock, times(9)).accept(observationCaptor.capture());

        // This list was manually validated, i.e. it is correct (!) given the job coordinates and dumpfile above
        assertThat(observationCaptor.getAllValues().stream().map(obs -> obs.det().getObjectId())).containsExactly(
            "da2aa810706137e09c23e7d80324ead9",
            "19295f0a2c443a11a51394dad7be5233",
            "d3b0b0dfa0eb3d269b81e38e4da1ca85",
            "5383a7053f91347c86515731882d3b43",
            "ae6565fbbb83354083bcf536e74ae409",
            "ed7a095e857135c1bc73416465d6e8e5",
            "85adfec29c6634f791d9d3e846b24663",
            "e2cc4567099f3712a6d56e1807794df2",
            "e8c7a510570f32fb9e09618178fb035f"
        );

    }

    static ObservationJobEntity prepareJobEntity(List<PointEntity> geometryPoints) {
        ObservationJobEntity entity = new ObservationJobEntity();
        entity.setCameraId("geomapper:stream1");
        entity.setDetectionClassId(2);
        entity.setGeometryPoints(geometryPoints);
        entity.setType(JobType.LINE_CROSSING);
        entity.setGeoReferenced(false);

        return entity;
    }

    static List<SaeMessageDto> createLinearTrajectory(Point2D start, Point2D end, int numSteps, Duration stepInterval) {
        List<SaeMessageDto> trajectory = new ArrayList<>();

        Instant startTime = Instant.now();

        trajectory.add(Helper.createSaeMsg(startTime, new Point2D.Double(start.getX(), start.getY()), "obj1"));
        double currentX = start.getX();
        double currentY = start.getY();
        
        for (int i = 0; i < numSteps; i++) {
            currentX += (end.getX() - start.getX()) / numSteps;
            currentY += (end.getY() - start.getY()) / numSteps;
            trajectory.add(Helper.createSaeMsg(startTime.plus(stepInterval.multipliedBy(i+1)), new Point2D.Double(currentX, currentY), "obj1"));
        }

        return trajectory;
    }

}
