package de.starwit.service.jobs;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import de.starwit.visionapi.Messages.Detection;
import de.starwit.visionapi.Messages.SaeMessage;

public class SaeDetectionDto {

    private Instant captureTs;

    private String cameraId;

    private String objectId;

    private Integer classId;

    private Double confidence;

    private Double minX;

    private Double minY;

    private Double maxX;

    private Double maxY;

    private Double latitude;

    private Double longitude;
    
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

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Double getMinX() {
        return minX;
    }

    public void setMinX(Double minX) {
        this.minX = minX;
    }

    public Double getMinY() {
        return minY;
    }

    public void setMinY(Double minY) {
        this.minY = minY;
    }

    public Double getMaxX() {
        return maxX;
    }

    public void setMaxX(Double maxX) {
        this.maxX = maxX;
    }

    public Double getMaxY() {
        return maxY;
    }

    public void setMaxY(Double maxY) {
        this.maxY = maxY;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public static List<SaeDetectionDto> from(SaeMessage msg) {
        List<SaeDetectionDto> results = new ArrayList<>();
        for (Detection det : msg.getDetectionsList()) {
            SaeDetectionDto dto = new SaeDetectionDto();
            dto.setCaptureTs(Instant.ofEpochMilli(msg.getFrame().getTimestampUtcMs()));
            dto.setCameraId(msg.getFrame().getSourceId());
            dto.setObjectId(HexFormat.of().formatHex(det.getObjectId().toByteArray()));
            dto.setClassId(det.getClassId());
            dto.setConfidence((double) det.getConfidence());
            dto.setMinX((double) det.getBoundingBox().getMinX());
            dto.setMinY((double) det.getBoundingBox().getMinY());
            dto.setMaxX((double) det.getBoundingBox().getMaxX());
            dto.setMaxY((double) det.getBoundingBox().getMaxY());
            dto.setLatitude(det.getGeoCoordinate().getLatitude());
            dto.setLongitude(det.getGeoCoordinate().getLongitude());
            results.add(dto);
        }
        return results;
    }

}
