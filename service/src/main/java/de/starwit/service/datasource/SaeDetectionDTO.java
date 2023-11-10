package de.starwit.service.datasource;

import java.time.ZonedDateTime;

import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.SqlResultSetMapping;

@SqlResultSetMapping(
    name = "SaeDetectionDTO",
    entities = {
        @EntityResult(entityClass = SaeDetectionDTO.class, fields = {
            @FieldResult(name = "detectionId", column = "DETECTION_ID"),
            @FieldResult(name = "captureTs", column = "CAPTURE_TS"),
            @FieldResult(name = "cameraId", column = "CAMERA_ID"),
            @FieldResult(name = "objectId", column = "OBJECT_ID"),
            @FieldResult(name = "classId", column = "CLASS_ID"),
            @FieldResult(name = "confidence", column = "CONFIDENCE"),
            @FieldResult(name = "minX", column = "MIN_X"),
            @FieldResult(name = "maxX", column = "MAX_X"),
            @FieldResult(name = "minY", column = "MIN_Y"),
            @FieldResult(name = "maxY", column = "MAX_Y")
        })
    }
)
public class SaeDetectionDTO {

    private Long detectionId;
    private ZonedDateTime captureTs;
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

    public ZonedDateTime getCaptureTs() {
        return captureTs;
    }

    public void setCaptureTs(ZonedDateTime captureTs) {
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

    

}
