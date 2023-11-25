package de.starwit.analytics.dtos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class SaeDetectionDto {

    private Long detectionId;
    private Instant captureTs;
    private String cameraId;
    private String objectId;
    private Integer classId;
    private Double confidence;
    private Double minX;
    private Double minY;
    private Double maxX;
    private Double maxY;

    public Long getDetectionId() {
        return detectionId;
    }

    public void setDetectionId(Long detectionId) {
        this.detectionId = detectionId;
    }

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

    public static SaeDetectionDto from(ResultSet rs) throws SQLException {
        SaeDetectionDto dto = new SaeDetectionDto();
        dto.setDetectionId(rs.getLong("DETECTION_ID"));
        dto.setCaptureTs(rs.getTimestamp("CAPTURE_TS").toInstant());
        dto.setCameraId(rs.getString("CAMERA_ID"));
        dto.setObjectId(rs.getString("OBJECT_ID"));
        dto.setClassId(rs.getInt("CLASS_ID"));
        dto.setConfidence(rs.getDouble("CONFIDENCE"));
        dto.setMinX(rs.getDouble("MIN_X"));
        dto.setMinY(rs.getDouble("MIN_Y"));
        dto.setMaxX(rs.getDouble("MAX_X"));
        dto.setMaxY(rs.getDouble("MAX_Y"));
        return dto;
    }

}
