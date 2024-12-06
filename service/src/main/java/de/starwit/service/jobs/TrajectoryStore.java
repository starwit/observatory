package de.starwit.service.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import de.starwit.service.sae.SaeDetectionDto;

/**
 * Provides storage for object trajectories (i.e. sequences of `SaeDetectionDto`)
 * Not thread-safe, should not be shared.
 */
public class TrajectoryStore {
    private HashMap<String, LinkedList<SaeDetectionDto>> trajectoryByObjId = new HashMap<>();
    private final Duration TARGET_WINDOW;
    private Instant mostRecentTimestamp;

    public TrajectoryStore(Duration targetWindow) {
        this.TARGET_WINDOW = targetWindow;
        this.mostRecentTimestamp = Instant.ofEpochSecond(0);
    }

    public void addDetection(SaeDetectionDto det) {
        LinkedList<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory == null) {
            trajectory = new LinkedList<>();
            trajectoryByObjId.put(det.getObjectId(), trajectory);
        }
        trajectory.addLast(det);

        if (det.getCaptureTs().isAfter(mostRecentTimestamp)) {
            this.mostRecentTimestamp = det.getCaptureTs();
        }
        truncateTrajectories();
    }

    public SaeDetectionDto getFirst(SaeDetectionDto det) {
        LinkedList<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory == null || trajectory.isEmpty()) {
            return null;
        }
        return trajectory.getFirst();
    }

    public SaeDetectionDto getLast(SaeDetectionDto det) {
        LinkedList<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory == null || trajectory.isEmpty()) {
            return null;
        }
        return trajectory.getLast();
    }

    public void removeFirst(SaeDetectionDto det) {
        LinkedList<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory != null) {
            trajectory.pollFirst();
        }
    }

    public void removeLast(SaeDetectionDto det) {
        LinkedList<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory != null) {
            trajectory.pollLast();
        }
    }

    public void clear(SaeDetectionDto det) {
        LinkedList<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory != null) {
            trajectory.clear();
        }
    }

    /**
     * Returns all trajectories that satisfy the target length (i.e. length > 0.80 * targetTrajectoryLength).
     * As the trajectories are truncated during addDetection(), trajectory starts must by within the analyzing window.
     * Trajectory length is defined by the time between the first and last detection in the trajectory.
     * The analyzing window is relative to the most recent timestamp we have seen so far, there is no tie to the actual time!
     * @return
     */
    public List<List<SaeDetectionDto>> getAllHealthyTrajectories() {
        List<List<SaeDetectionDto>> trajectories = new ArrayList<>();
        for (LinkedList<SaeDetectionDto> trajectory : trajectoryByObjId.values()) {
            if (isHealthy(trajectory)) {
                trajectories.add(new ArrayList<>(trajectory));
            }
        }
        return trajectories;
    }
    
    private boolean isHealthy(Deque<SaeDetectionDto> trajectory) {
        return !trajectory.isEmpty() && trajectoryLength(trajectory).toMillis() > 0.8 * TARGET_WINDOW.toMillis();
    }

    public Instant getMostRecentTimestamp() {
        return this.mostRecentTimestamp;
    }

    private void truncateTrajectories() {
        for (Deque<SaeDetectionDto> trajectory : this.trajectoryByObjId.values()) {
            while (!trajectory.isEmpty() && trajectory.getFirst().getCaptureTs().isBefore(this.mostRecentTimestamp.minus(TARGET_WINDOW))) {
                trajectory.pollFirst();
            }
        }
    }

    private Duration trajectoryLength(Deque<SaeDetectionDto> trajectory) {
        return Duration.between(trajectory.getFirst().getCaptureTs(), trajectory.getLast().getCaptureTs());
    }

    /**
     * Drops all trajectories whose most recent data point is older than (referenceTime - trajectoryLength),
     * i.e. trajectories that would be truncated completely if a data point was to be added.
     * @param referenceTime
     */
    public void purge(Instant referenceTime) {
        List<String> keysToDelete = new ArrayList<>();
        Instant cutOff = referenceTime.minus(TARGET_WINDOW);
        
        for (Entry<String, LinkedList<SaeDetectionDto>> entry: trajectoryByObjId.entrySet()) {
            LinkedList<SaeDetectionDto> trajectory = entry.getValue();
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
