package de.starwit.service.sae;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enforces strictly monotonic timestamps per stream key. Every stream is a separate monotonicity domain.
 * Thread-safe, i.e. can be called concurrently for different (and the same) streams.
 */
public class StreamMonotonicityFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ConcurrentHashMap<String, Long> lastAcceptedTsByStream = new ConcurrentHashMap<>();

    /**
     * Returns true if `timestampMs` is strictly greater than the last accepted timestamp on `streamKey`
     * (or if it is the first message on that stream) and records it as the new last accepted timestamp.
     * Returns false otherwise and leaves the state unchanged.
     */
    public boolean accept(String streamKey, long timestampMs) {
        AtomicBoolean accepted = new AtomicBoolean(false);
        lastAcceptedTsByStream.compute(streamKey, (key, lastTs) -> {
            if (lastTs == null || timestampMs > lastTs) {
                accepted.set(true);
                return timestampMs;
            }
            log.warn("Discarding message on stream {} with timestamp {} (not after last accepted timestamp {})",
                    key, Instant.ofEpochMilli(timestampMs), Instant.ofEpochMilli(lastTs));
            return lastTs;
        });
        return accepted.get();
    }
}
