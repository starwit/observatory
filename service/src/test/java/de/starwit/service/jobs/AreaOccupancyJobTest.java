package de.starwit.service.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.geom.Point2D;
import java.io.IOException;
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
import de.starwit.service.sae.SaeMessageDto;
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
                Helper.createPoint(0, 100)));

        // The dummy entries just serve to trigger the event-based processing in the job
        List<SaeMessageDto> messages = Arrays.asList(
                Helper.createSaeMsg(Instant.ofEpochMilli(0000), new Point2D.Double(50, 50), "dummy"),

                Helper.createSaeMsg(Instant.ofEpochMilli(1000), new Point2D.Double(50, 50), "obj1"),
                Helper.createSaeMsg(Instant.ofEpochMilli(1000), new Point2D.Double(50, 50), "obj2"),
                Helper.createSaeMsg(Instant.ofEpochMilli(1000), new Point2D.Double(50, 200), "obj3"),

                Helper.createSaeMsg(Instant.ofEpochMilli(3000), new Point2D.Double(50, 50), "obj1"),
                Helper.createSaeMsg(Instant.ofEpochMilli(3000), new Point2D.Double(50, 50), "obj2"),
                Helper.createSaeMsg(Instant.ofEpochMilli(3000), new Point2D.Double(50, 50), "obj3"),
                Helper.createSaeMsg(Instant.ofEpochMilli(3000), new Point2D.Double(50, 200), "obj4"),

                Helper.createSaeMsg(Instant.ofEpochMilli(9900), new Point2D.Double(50, 50), "obj1"),
                Helper.createSaeMsg(Instant.ofEpochMilli(9900), new Point2D.Double(50, 50), "obj2"),

                Helper.createSaeMsg(Instant.ofEpochMilli(10100), new Point2D.Double(50, 50), "dummy"));

        AreaOccupancyJob testee = new AreaOccupancyJob(jobEntity, Duration.ofSeconds(10), 0.001, 0.1,
                observationConsumerMock);

        for (SaeMessageDto msg : messages) {
            testee.processNewMessage(msg);
        }

        ArgumentCaptor<AreaOccupancyObservation> observationCaptor = ArgumentCaptor
                .forClass(AreaOccupancyObservation.class);

        verify(observationConsumerMock, times(2)).accept(observationCaptor.capture());
        assertThat(observationCaptor.getValue().occupancyTime().toInstant().toEpochMilli()).isEqualTo(10100);
        assertThat(observationCaptor.getValue().count()).isEqualTo(2);

        testee.processNewMessage(
                Helper.createSaeMsg(Instant.ofEpochMilli(10100), new Point2D.Double(50, 50), "dummy2"));
        verifyNoMoreInteractions(observationConsumerMock);
    }

    @Test
    public void testAreaOccupancyDump() throws IOException {
        // The entire frame
        ObservationJobEntity jobEntity = prepareJobEntity(Arrays.asList(
                Helper.createPoint(0, 0),
                Helper.createPoint(1, 0),
                Helper.createPoint(1, 1),
                Helper.createPoint(0, 1)));

        AreaOccupancyJob testee = new AreaOccupancyJob(jobEntity, Duration.ofSeconds(10), 0.001, 0.1,
                observationConsumerMock);

        try (SaeDump saeDump = new SaeDump(Paths.get("src/test/resources/test.saedump"))) {
            for (SaeMessage msg : saeDump) {
                testee.processNewMessage(SaeMessageDto.from(msg));
            }
        }

        ArgumentCaptor<AreaOccupancyObservation> observationCaptor = ArgumentCaptor
                .forClass(AreaOccupancyObservation.class);

        verify(observationConsumerMock, times(6)).accept(observationCaptor.capture());

        assertThat(observationCaptor.getAllValues().stream().map(o -> o.count())).containsExactly(0L, 23L, 19L, 19L,
                21L, 21L);
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
