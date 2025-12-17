package de.starwit.service.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.service.sae.SaeDetectionDto;

/**
 * Provides storage for object trajectories (i.e. sequences of `SaeDetectionDto`)
 * of one camera stream. This must not be used across multiple camera streams!
 * Not thread-safe, should not be shared across threads without synchronization.
 */
public class TrajectoryStore {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private HashMap<String, LinkedList<SaeDetectionDto>> trajectoryByObjId = new HashMap<>();

    public TrajectoryStore() {}

    /**
     * Adds a detection to the trajectory of its object ID.
     * Enforces timestamp monotonicity per object ID. Will reject detections otherwise.
     */
    public void addDetection(SaeDetectionDto det) {
        if (det == null) {
            log.warn("Ignoring null detection");
            return;
        } 

        SaeDetectionDto lastDetection = this.getLast(det);
        if (lastDetection != null && det.getCaptureTs().isBefore(lastDetection.getCaptureTs())) {
            log.warn("Ignoring detection with timestamp {} on stream {} as it breaks monotonicity (last timestamp for stream is {})", det.getCameraId(), det.getCaptureTs(), lastDetection.getCaptureTs());
            return;
        }

        LinkedList<SaeDetectionDto> trajectory = trajectoryByObjId.computeIfAbsent(det.getObjectId(), k -> new LinkedList<>());
        trajectory.addLast(det);
    }

    public boolean hasTrajectory(SaeDetectionDto det) {
        return this.trajectoryByObjId.get(det.getObjectId()) != null;
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

    public List<List<SaeDetectionDto>> getAll() {
        return this.trajectoryByObjId.values().stream().map(Collections::unmodifiableList).toList();
    }

    public void clear(SaeDetectionDto det) {
        LinkedList<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory != null) {
            trajectory.clear();
        }
    }
    
    /**
     * Trims the trajectory of `det` to the given target length, counting backwards from the most recent detection.
     * Does not alter other trajectories.
     * @param det
     * @param targetLength
     */
    public void trimSingleRelative(SaeDetectionDto det, Duration targetLength) {
        String objId = det.getObjectId();
        LinkedList<SaeDetectionDto> trajectory = this.trajectoryByObjId.get(objId);
        if (trajectory == null) {
            return;
        }
        
        trimInternal(trajectory, trajectory.getLast().getCaptureTs().minus(targetLength));
        tryPurge(objId);
    }

    /**
     * Trims all trajectories to the given cut-off time, removing all detections older than `cutOff`.
     * @param cutOff
     */
    public void trimAllAbsolute(Instant cutOff) {
        Set<String> objectIds = new HashSet<>(this.trajectoryByObjId.keySet());
        for (String objectId : objectIds) {
            trimInternal(this.trajectoryByObjId.get(objectId), cutOff);
            tryPurge(objectId);
        }
    }

    private void trimInternal(LinkedList<SaeDetectionDto> trajectory, Instant cutOff) {
        while (trajectory != null && !trajectory.isEmpty() && trajectory.getFirst().getCaptureTs().isBefore(cutOff)) {
            trajectory.pollFirst();
            
        }
    }

    private void tryPurge(String objId) {
        LinkedList<SaeDetectionDto> trajectory = this.trajectoryByObjId.get(objId);
        if (trajectory != null && trajectory.isEmpty()) {
            this.trajectoryByObjId.remove(objId);
        }
    }

    public Duration length(SaeDetectionDto det) {
        LinkedList<SaeDetectionDto> trajectory = this.trajectoryByObjId.get(det.getObjectId());
        if (trajectory == null || trajectory.isEmpty()) {
            return Duration.ZERO;
        }
        return Duration.between(trajectory.getFirst().getCaptureTs(), trajectory.getLast().getCaptureTs());
    }

    /**
     * Drops all trajectories whose most recent data point is older than `cutOffTime`.
     * @param cutOffTime
     */
    public void purge(Instant cutOffTime) {
        List<String> keysToDelete = new ArrayList<>();
        
        for (Entry<String, LinkedList<SaeDetectionDto>> entry: trajectoryByObjId.entrySet()) {
            LinkedList<SaeDetectionDto> trajectory = entry.getValue();
            if (trajectory.isEmpty() || trajectory.getLast().getCaptureTs().isBefore(cutOffTime)) {
                keysToDelete.add(entry.getKey());
            }
        }

        keysToDelete.forEach(key -> trajectoryByObjId.remove(key));
    }

    public int count() {
        return trajectoryByObjId.size();
    }
}
