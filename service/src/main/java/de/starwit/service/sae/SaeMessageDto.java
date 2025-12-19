package de.starwit.service.sae;

import java.time.Instant;
import java.util.List;

import de.starwit.visionapi.Sae.SaeMessage;

public class SaeMessageDto {

    private Instant captureTs;

    private String cameraId;

    private List<SaeDetectionDto> detections;

    public Instant getCaptureTs() {
        return captureTs;
    }

    public void setCaptureTs(Instant captureTs) {
        this.captureTs = captureTs;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public List<SaeDetectionDto> getDetections() {
        return detections;
    }

    public void setDetections(List<SaeDetectionDto> detections) {
        this.detections = detections;
    }

    @Override
    public String toString() {
        return "SaeMessageDto(" + cameraId + ", " + captureTs.toString() + ")";
    }

    public static SaeMessageDto from(SaeMessage msg) {
        List<SaeDetectionDto> detections = SaeDetectionDto.from(msg);
        
        SaeMessageDto dto = new SaeMessageDto();
        dto.setCaptureTs(Instant.ofEpochMilli(msg.getFrame().getTimestampUtcMs()));
        dto.setCameraId(msg.getFrame().getSourceId());
        dto.setDetections(detections);
        return dto;
    }

}
