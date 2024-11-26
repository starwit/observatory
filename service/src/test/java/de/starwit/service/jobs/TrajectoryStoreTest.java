package de.starwit.service.jobs;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TrajectoryStoreTest {
    
    @Test
    public void testLengthCalculation() {
        TrajectoryStore testee = new TrajectoryStore(Duration.ofMillis(1000));

        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(100), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(300), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(500), null, "o1"));

        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(100), null, "o2"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(500), null, "o2"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(1100), null, "o2"));

        assertThat(testee.getAllValidTrajectories().size()).isEqualTo(1);
    }
}
