package de.starwit.service.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class FrameCounterTest {

    @Test
    public void testUnknownJobIsZero() {
        FrameCounter testee = new FrameCounter();

        assertThat(testee.get(1L)).isEqualTo(0);
    }

    @Test
    public void testCountsPerJob() {
        FrameCounter testee = new FrameCounter();

        testee.increment(1L);
        testee.increment(1L);
        testee.increment(2L);

        assertThat(testee.get(1L)).isEqualTo(2);
        assertThat(testee.get(2L)).isEqualTo(1);
    }
}
