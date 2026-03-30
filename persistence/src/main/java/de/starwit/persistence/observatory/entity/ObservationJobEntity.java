package de.starwit.persistence.observatory.entity;

import java.math.BigDecimal;
import java.util.List;

import de.starwit.persistence.common.entity.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "observation_job")
public class ObservationJobEntity extends AbstractEntity<Long> {

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "observation_area_id")
    private Long observationAreaId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private JobType type;

    @NotNull
    @Column(name = "stream_key")
    private String streamKey;

    @NotNull
    @Column(name = "detection_class_id")
    private Integer detectionClassId;

    @NotNull
    @Column(name = "enabled")
    private Boolean enabled;

    @NotNull
    @Column(name = "classification")
    private String classification;

    @NotNull
    @Column(name = "geo_referenced")
    private Boolean geoReferenced;

    @Column(name = "center_longitude")
    private BigDecimal centerLongitude;

    @Column(name = "center_latitude")
    private BigDecimal centerLatitude;

    @Column(name = "direction")
    private String direction;

    @Column(name = "max_count")
    private Integer maxCount;

    @NotNull
    @OneToMany(mappedBy = "observationJob", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("orderIdx ASC")
    private List<PointEntity> geometryPoints;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getObservationAreaId() {
        return observationAreaId;
    }

    public void setObservationAreaId(Long observationAreaId) {
        this.observationAreaId = observationAreaId;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public String getStreamKey() {
        return streamKey;
    }

    public void setStreamKey(String streamKey) {
        this.streamKey = streamKey;
    }

    public Integer getDetectionClassId() {
        return detectionClassId;
    }

    public void setDetectionClassId(Integer detectionClassId) {
        this.detectionClassId = detectionClassId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<PointEntity> getGeometryPoints() {
        return geometryPoints;
    }

    public void setGeometryPoints(List<PointEntity> geometryPoints) {
        this.geometryPoints = geometryPoints;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public Boolean getGeoReferenced() {
        return geoReferenced;
    }

    public void setGeoReferenced(Boolean geoReferenced) {
        this.geoReferenced = geoReferenced;
    }

    public BigDecimal getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(BigDecimal centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public BigDecimal getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(BigDecimal centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

}
