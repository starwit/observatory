package de.starwit.service.jobs;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TrajectoryStore {
    private Map<String, LinkedList<SaeDetectionDto>> trajectoryByObjId = new HashMap<>();

    public void addDetection(SaeDetectionDto det) {
        if (trajectoryByObjId.get(det.getObjectId()) == null) {
            LinkedList<SaeDetectionDto> trajectory = new LinkedList<>();
            trajectoryByObjId.put(det.getObjectId(), trajectory);
        }
        trajectoryByObjId.get(det.getObjectId()).addLast(det);
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

    public void purge(Duration maxAge) {
        List<String> keysToDelete = new ArrayList<>();
        Instant cutOff = Instant.now().minus(maxAge);
        for (Entry<String, LinkedList<SaeDetectionDto>> entry: trajectoryByObjId.entrySet()) {
            if (entry.getValue().isEmpty() || entry.getValue().getLast().getCaptureTs().isBefore(cutOff)) {
                keysToDelete.add(entry.getKey());
            }
        }
        keysToDelete.forEach(key -> trajectoryByObjId.remove(key));
    }

    public int size() {
        return trajectoryByObjId.size();
    }
}
