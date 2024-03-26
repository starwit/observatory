package de.starwit.persistence.databackend.entity;

import java.util.List;

import de.starwit.persistence.common.entity.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "observation_job")
public class ObservationJobEntity extends AbstractEntity<Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "observation_area_id")
    private Long observationAreaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private JobType type;

    @Column(name = "camera_id")
    private String cameraId;

    @Column(name = "detection_class_id")
    private Integer detectionClassId;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "classification")
    private String classification;

    @Column(name = "geo_referenced")
    private Boolean geoReferenced;

    @OneToMany(mappedBy = "observationJob", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
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
}
