package de.starwit.service.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import de.starwit.service.sae.SaeDetectionDto;

/**
 * Provides storage for object trajectories (i.e. sequences of `SaeDetectionDto`)
 * All contained data structures are thread-safe, so this can be shared among threads.
 * However, there are no guarantees that operations see the most recent updates.
 */
public class TrajectoryStore {
    private ConcurrentHashMap<String, ConcurrentLinkedDeque<SaeDetectionDto>> trajectoryByObjId = new ConcurrentHashMap<>();
    private final Duration MAX_TRAJECTORY_LENGTH;
    private Instant mostRecentTimestamp;

    public TrajectoryStore(Duration maxTrajectoryLength) {
        this.MAX_TRAJECTORY_LENGTH = maxTrajectoryLength;
        this.mostRecentTimestamp = Instant.ofEpochSecond(0);
    }

    public void addDetection(SaeDetectionDto det) {
        ConcurrentLinkedDeque<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory == null) {
            trajectory = new ConcurrentLinkedDeque<>();
            trajectoryByObjId.put(det.getObjectId(), trajectory);
        }
        trajectory.addLast(det);
        truncateTrajectory(trajectory);
        this.mostRecentTimestamp = det.getCaptureTs();
    }

    public SaeDetectionDto getFirst(SaeDetectionDto det) {
        ConcurrentLinkedDeque<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory == null || trajectory.isEmpty()) {
            return null;
        }
        return trajectory.getFirst();
    }

    public SaeDetectionDto getLast(SaeDetectionDto det) {
        ConcurrentLinkedDeque<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory == null || trajectory.isEmpty()) {
            return null;
        }
        return trajectory.getLast();
    }

    public void removeFirst(SaeDetectionDto det) {
        ConcurrentLinkedDeque<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory != null) {
            trajectory.pollFirst();
        }
    }

    public void removeLast(SaeDetectionDto det) {
        ConcurrentLinkedDeque<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory != null) {
            trajectory.pollLast();
        }
    }

    public void clear(SaeDetectionDto det) {
        ConcurrentLinkedDeque<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory != null) {
            trajectory.clear();
        }
    }

    public List<List<SaeDetectionDto>> getAllTrajectories() {
        List<List<SaeDetectionDto>> trajectories = new ArrayList<>();
        for (Entry<String, ConcurrentLinkedDeque<SaeDetectionDto>> entry : trajectoryByObjId.entrySet()) {
            trajectories.add(new ArrayList<>(entry.getValue()));
        }
        return trajectories;
    }

    public Instant getMostRecentTimestamp() {
        return this.mostRecentTimestamp;
    }

    private void truncateTrajectory(ConcurrentLinkedDeque<SaeDetectionDto> trajectory) {
        while (trajectoryLength(trajectory).minus(MAX_TRAJECTORY_LENGTH).isPositive()) {
            trajectory.pollFirst();
        }
    }

    private Duration trajectoryLength(ConcurrentLinkedDeque<SaeDetectionDto> trajectory) {
        return Duration.between(trajectory.getFirst().getCaptureTs(), trajectory.getLast().getCaptureTs());
    }

    /**
     * Drops all trajectories whose most recent data point is older than (referenceTime - trajectoryLength),
     * i.e. trajectories that would be truncated completely if a data point was to be added.
     * @param referenceTime
     */
    public void purge(Instant referenceTime) {
        List<String> keysToDelete = new ArrayList<>();
        Instant cutOff = referenceTime.minus(MAX_TRAJECTORY_LENGTH);
        
        for (Entry<String, ConcurrentLinkedDeque<SaeDetectionDto>> entry: trajectoryByObjId.entrySet()) {
            ConcurrentLinkedDeque<SaeDetectionDto> trajectory = entry.getValue();
            if (trajectory.isEmpty() || trajectory.getLast().getCaptureTs().isBefore(cutOff)) {
                keysToDelete.add(entry.getKey());
            }
        }

        keysToDelete.forEach(key -> trajectoryByObjId.remove(key));
    }

    public void purge() {
        this.purge(this.mostRecentTimestamp);
    }

    public int size() {
        return trajectoryByObjId.size();
    }
}
