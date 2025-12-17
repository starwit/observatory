package de.starwit.service.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import de.starwit.service.sae.SaeDetectionDto;

public class TrajectoryStoreTest {
    
    @Test
    public void testAdd() {
        TrajectoryStore testee = new TrajectoryStore(Duration.ofMillis(1000));

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
    public void testPurge() {
        TrajectoryStore testee = new TrajectoryStore(Duration.ofMillis(1000));

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
}
