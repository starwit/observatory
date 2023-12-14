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
@Table(name = "analytics_job")
public class AnalyticsJobEntity extends AbstractEntity<Long> {

    @Column(name = "name")
    private String name;

    @Column(name = "parkingareaid")
    private Long parkingAreaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private JobType type;

    @Column(name = "cameraid")
    private String cameraId;

    @Column(name = "detectionclassid")
    private Integer detectionClassId;

    @Column(name = "enabled")
    private Boolean enabled;

    @OneToMany(mappedBy = "analyticsJob", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PointEntity> geometryPoints;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParkingAreaId() {
        return parkingAreaId;
    }

    public void setParkingAreaId(Long parkingAreaId) {
        this.parkingAreaId = parkingAreaId;
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

}
