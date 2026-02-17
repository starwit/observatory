package de.starwit.service.sae;

import java.time.Instant;
import java.util.List;

import de.starwit.visionapi.Sae.SaeMessage;

public class SaeMessageDto {

    private Instant captureTs;

    private String streamKey;
    
    private String sourceId;

    private List<SaeDetectionDto> detections;

    public Instant getCaptureTs() {
        return captureTs;
    }

    public void setCaptureTs(Instant captureTs) {
        this.captureTs = captureTs;
    }
    
    public String getStreamKey() {
        return streamKey;
    }

    public void setStreamKey(String streamKey) {
        this.streamKey = streamKey;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String cameraId) {
        this.sourceId = cameraId;
    }

    public List<SaeDetectionDto> getDetections() {
        return detections;
    }

    public void setDetections(List<SaeDetectionDto> detections) {
        this.detections = detections;
    }

    @Override
    public String toString() {
        return "SaeMessageDto(" + sourceId + ", " + captureTs.toString() + ")";
    }

    public static SaeMessageDto from(String streamKey, SaeMessage msg) {
        List<SaeDetectionDto> detections = SaeDetectionDto.from(streamKey, msg);
        
        SaeMessageDto dto = new SaeMessageDto();
        dto.setCaptureTs(Instant.ofEpochMilli(msg.getFrame().getTimestampUtcMs()));
        dto.setSourceId(msg.getFrame().getSourceId());
        dto.setStreamKey(streamKey);
        dto.setDetections(detections);
        return dto;
    }

}
