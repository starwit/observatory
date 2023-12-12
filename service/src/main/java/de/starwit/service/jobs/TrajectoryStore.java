package de.starwit.service.jobs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.starwit.persistence.sae.entity.SaeDetectionEntity;

public class TrajectoryStore {
    private Map<String, LinkedList<SaeDetectionEntity>> trajectoryByObjId = new HashMap<>();

    public void addDetection(SaeDetectionEntity det) {
        if (trajectoryByObjId.get(det.getObjectId()) == null) {
            LinkedList<SaeDetectionEntity> trajectory = new LinkedList<>();
            trajectoryByObjId.put(det.getObjectId(), trajectory);
        }
        trajectoryByObjId.get(det.getObjectId()).addLast(det);
    }

    public SaeDetectionEntity getFirst(SaeDetectionEntity det) {
        LinkedList<SaeDetectionEntity> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory == null || trajectory.isEmpty()) {
            return null;
        }
        return trajectory.getFirst();
    }

    public SaeDetectionEntity getLast(SaeDetectionEntity det) {
        LinkedList<SaeDetectionEntity> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory == null || trajectory.isEmpty()) {
            return null;
        }
        return trajectory.getLast();
    }

    public void removeFirst(SaeDetectionEntity det) {
        LinkedList<SaeDetectionEntity> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory != null) {
            trajectory.pollFirst();
        }
    }

    public void removeLast(SaeDetectionEntity det) {
        LinkedList<SaeDetectionEntity> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory != null) {
            trajectory.pollLast();
        }
    }

    public void clear(SaeDetectionEntity det) {
        LinkedList<SaeDetectionEntity> trajectory = trajectoryByObjId.get(det.getObjectId());
        if (trajectory != null) {
            trajectory.clear();
        }
    }
}
