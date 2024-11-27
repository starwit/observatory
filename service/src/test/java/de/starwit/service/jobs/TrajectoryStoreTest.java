package de.starwit.service.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.starwit.service.sae.SaeDetectionDto;

import static org.assertj.core.api.Assertions.assertThat;

public class TrajectoryStoreTest {
    
    @Test
    public void testLengthCalculation() {
        TrajectoryStore testee = new TrajectoryStore(Duration.ofMillis(1000));

        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(100), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(300), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(600), null, "o1"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(1100), null, "o1"));
        
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(500), null, "o2"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(800), null, "o2"));
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(1500), null, "o2"));
        
        List<List<SaeDetectionDto>> validTrajectories = testee.getAllValidTrajectories();
        assertThat(validTrajectories).hasSize(1);
        assertThat(validTrajectories.get(0).get(0).getObjectId()).isEqualTo("o2");
        
        testee.addDetection(Helper.createDetection(Instant.ofEpochMilli(1600), null, "o1"));

        validTrajectories = testee.getAllValidTrajectories();
        assertThat(validTrajectories).hasSize(1);
        assertThat(validTrajectories.get(0).get(0).getObjectId()).isEqualTo("o1");
    }
}
