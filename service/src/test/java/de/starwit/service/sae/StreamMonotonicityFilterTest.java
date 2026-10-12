package de.starwit.service.sae;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class StreamMonotonicityFilterTest {

    @Test
    public void testAcceptsFirstAndIncreasing() {
        StreamMonotonicityFilter testee = new StreamMonotonicityFilter();

        assertThat(testee.accept("s1", 100)).isTrue();
        assertThat(testee.accept("s1", 200)).isTrue();
        assertThat(testee.accept("s1", 300)).isTrue();
    }

    @Test
    public void testRejectsOlderAndKeepsLastAccepted() {
        StreamMonotonicityFilter testee = new StreamMonotonicityFilter();

        assertThat(testee.accept("s1", 100)).isTrue();
        assertThat(testee.accept("s1", 300)).isTrue();
        assertThat(testee.accept("s1", 200)).isFalse();
        // 250 is still before the last accepted timestamp (300)
        assertThat(testee.accept("s1", 250)).isFalse();
        assertThat(testee.accept("s1", 400)).isTrue();
    }

    @Test
    public void testRejectsEqual() {
        StreamMonotonicityFilter testee = new StreamMonotonicityFilter();

        assertThat(testee.accept("s1", 100)).isTrue();
        assertThat(testee.accept("s1", 100)).isFalse();
    }

    @Test
    public void testStreamsAreIndependent() {
        StreamMonotonicityFilter testee = new StreamMonotonicityFilter();

        assertThat(testee.accept("s1", 500)).isTrue();
        assertThat(testee.accept("s2", 100)).isTrue();
        assertThat(testee.accept("s2", 200)).isTrue();
        assertThat(testee.accept("s1", 400)).isFalse();
    }
}
