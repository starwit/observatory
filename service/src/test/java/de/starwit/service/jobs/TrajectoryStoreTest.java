package de.starwit.service.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import de.starwit.service.sae.SaeDetectionDto;

public class TrajectoryStoreTest {
    
    @Test
    public void testAdd() {
        TrajectoryStore testee = new TrajectoryStore();

        SaeDetectionDto firstO1 = Helper.createDetection(Instant.ofEpochMilli(100), null, "o1");
        SaeDetectionDto lastO1 = Helper.createDetection(Instant.ofEpochMilli(200), null, "o1");

        testee.addDetection(firstO1);
        testee.addDetection(lastO1);

        SaeDetectionDto firstO2 = Helper.createDetection(Instant.ofEpochMilli(500), null, "o2");
        SaeDetectionDto lastO2 = Helper.createDetection(Instant.ofEpochMilli(800), null, "o2");
        
        testee.addDetection(firstO2);
        testee.addDetection(lastO2);

        SaeDetectionDto someO1 = Helper.createDetection(null, null, "o1");
        SaeDetectionDto someO2 = Helper.createDetection(null, null, "o2");

        assertThat(testee.hasTrajectory(someO1)).isTrue();
        assertThat(testee.hasTrajectory(someO2)).isTrue();

        assertThat(testee.getFirst(someO1)).isEqualTo(firstO1);
        assertThat(testee.getLast(someO1)).isEqualTo(lastO1);
        assertThat(testee.getFirst(someO2)).isEqualTo(firstO2);
        assertThat(testee.getLast(someO2)).isEqualTo(lastO2);
    }

    @Test
    public void testAddRejectsOutOfOrder() {
        TrajectoryStore testee = new TrajectoryStore();
        
        SaeDetectionDto first = Helper.createDetection(Instant.ofEpochMilli(200), null, "o1");
        SaeDetectionDto wrong = Helper.createDetection(Instant.ofEpochMilli(100), null, "o1");
        
        testee.addDetection(first);
        testee.addDetection(wrong); // should be rejected
        
        SaeDetectionDto some = Helper.createDetection(null, null, "o1");

        // Verify that the second detection was rejected
        assertThat(testee.getFirst(some)).isEqualTo(first);
        assertThat(testee.getLast(some)).isEqualTo(first);
    }
    
    @Test
    public void testPurge() {        
        TrajectoryStore testee = new TrajectoryStore();

        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(100), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(300), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(600), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(1100), null, "o1"));
        
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(500), null, "o2"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(800), null, "o2"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(1500), null, "o2"));
        
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(1600), null, "o1"));

        testee.purge(Instant.ofEpochMilli(1550));

        assertThat(testee.hasTrajectory(Helper.createDetection(null, null, "o1"))).isTrue();
        assertThat(testee.hasTrajectory(Helper.createDetection(null, null, "o2"))).isFalse();

    }

    @Test
    public void testTrimSingleRelative() {
        Duration targetWindow = Duration.ofMillis(500);
        TrajectoryStore testee = new TrajectoryStore();

        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(200), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(400), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(600), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(800), null, "o1"));

        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(200), null, "o2"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(400), null, "o2"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(600), null, "o2"));

        testee.trimSingleRelative(Helper.createDetection(null, null, "o1"), targetWindow);

        SaeDetectionDto first = testee.getFirst(Helper.createDetection(null, null, "o1"));;
        SaeDetectionDto last = testee.getLast(Helper.createDetection(null, null, "o1"));
        assertThat(first.getCaptureTs()).isEqualTo(Instant.ofEpochMilli(400));
        assertThat(last.getCaptureTs()).isEqualTo(Instant.ofEpochMilli(800));

        SaeDetectionDto firstO2 = testee.getFirst(Helper.createDetection(null, null, "o2"));;
        SaeDetectionDto lastO2 = testee.getLast(Helper.createDetection(null, null, "o2"));
        assertThat(firstO2.getCaptureTs()).isEqualTo(Instant.ofEpochMilli(200));
        assertThat(lastO2.getCaptureTs()).isEqualTo(Instant.ofEpochMilli(600));
    }

    @Test
    public void testTrimAllAbsolute() {
        TrajectoryStore testee = new TrajectoryStore();

        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(200), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(400), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(600), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(800), null, "o1"));

        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(200), null, "o2"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(400), null, "o2"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(600), null, "o2"));

        testee.trimAllAbsolute(Instant.ofEpochMilli(500));

        SaeDetectionDto firstO1 = testee.getFirst(Helper.createDetection(null, null, "o1"));;
        SaeDetectionDto lastO1 = testee.getLast(Helper.createDetection(null, null, "o1"));
        assertThat(firstO1.getCaptureTs()).isEqualTo(Instant.ofEpochMilli(600));
        assertThat(lastO1.getCaptureTs()).isEqualTo(Instant.ofEpochMilli(800));

        SaeDetectionDto firstO2 = testee.getFirst(Helper.createDetection(null, null, "o2"));;
        SaeDetectionDto lastO2 = testee.getLast(Helper.createDetection(null, null, "o2"));
        assertThat(firstO2.getCaptureTs()).isEqualTo(Instant.ofEpochMilli(600));
        assertThat(lastO2.getCaptureTs()).isEqualTo(Instant.ofEpochMilli(600));
    }
}
