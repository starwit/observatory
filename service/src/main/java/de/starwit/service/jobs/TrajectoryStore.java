package de.starwit.service.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.starwit.service.sae.SaeDetectionDto;

/**
 * Provides storage for object trajectories (i.e. sequences of `SaeDetectionDto`)
 * Not thread-safe, should not be shared.
 */
public class TrajectoryStore {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private HashMap<String, LinkedList<SaeDetectionDto>> trajectoryByObjId = new HashMap<>();
    protected final Duration TARGET_WINDOW;

    public TrajectoryStore(Duration targetWindow) {
        this.TARGET_WINDOW = targetWindow;
    }

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

    public void clear(SaeDetectionDto det) {
        LinkedList<SaeDetectionDto> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory != null) {
            trajectory.clear();
        }
    }
    
    public void trimTrajectory(SaeDetectionDto det) {
        trim(det.getObjectId());
    }

    public void trimAll() {
        for (String objId : this.trajectoryByObjId.keySet()) {
            trim(objId);
        }
    }

    private void trim(String objId) {
        LinkedList<SaeDetectionDto> trajectory = this.trajectoryByObjId.get(objId);
        if (trajectory == null) {
            return;
        }

        while (!trajectory.isEmpty() && trajectory.getFirst().getCaptureTs().isBefore(trajectory.getLast().getCaptureTs().minus(TARGET_WINDOW))) {
            trajectory.pollFirst();
        }
    }

    public Duration trajectoryLength(SaeDetectionDto det) {
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

    public int size() {
        return trajectoryByObjId.size();
    }
}
