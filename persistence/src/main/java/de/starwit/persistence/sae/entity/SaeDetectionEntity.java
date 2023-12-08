package de.starwit.persistence.sae.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import de.starwit.persistence.common.entity.AbstractCaptureEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SaeDetectionEntity implements AbstractCaptureEntity {

    @Id
    @Column(name = "capture_ts")
    private Instant captureTs;

    @Column(name = "detection_id")
    private Long detectionId;

    @Column(name = "camera_id")
    private String cameraId;

    @Column(name = "object_id")
    private String objectId;

    @Column(name = "class_id")
    private Integer classId;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "min_x")
    private Double minX;

    @Column(name = "min_y")
    private Double minY;

    @Column(name = "max_x")
    private Double maxX;

    @Column(name = "max_y")
    private Double maxY;

    public Instant getCaptureTs() {
        return captureTs;
    }

    public void setCaptureTs(Instant captureTs) {
        this.captureTs = captureTs;
    }

    public Long getDetectionId() {
        return detectionId;
    }

    public void setDetectionId(Long detectionId) {
        this.detectionId = detectionId;
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

    public static SaeDetectionEntity from(ResultSet rs) throws SQLException {
        SaeDetectionEntity dto = new SaeDetectionEntity();
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
