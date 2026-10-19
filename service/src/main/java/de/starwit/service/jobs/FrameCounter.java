package de.starwit.service.jobs;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

/**
 * Counts processed messages (frames) per observation job. In-memory only, i.e. counts are lost on restart.
 * Lives outside of the job instances, because these are recreated on every job refresh.
 */
@Component
public class FrameCounter {

    private final ConcurrentHashMap<Long, AtomicLong> countByJobId = new ConcurrentHashMap<>();

    public void increment(Long jobId) {
        countByJobId.computeIfAbsent(jobId, id -> new AtomicLong()).incrementAndGet();
    }

    public long get(Long jobId) {
        AtomicLong count = countByJobId.get(jobId);
        return count == null ? 0 : count.get();
    }
}
