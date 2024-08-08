package de.starwit.persistence.analytics.entity;

import java.math.BigDecimal;
import java.util.List;

import de.starwit.persistence.common.entity.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "metadata")
public class MetadataEntity extends AbstractEntity<Long> {

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "classification")
    private String classification;

    @Column(name = "geo_referenced")
    private Boolean geoReferenced;

    @Column(name = "center_longitude")
    private BigDecimal centerLongitude;

    @Column(name = "center_latitude")
    private BigDecimal centerLatitude;

    @Column(name = "observation_area_id")
    private Long observationAreaId;

    @OneToMany(mappedBy = "metadata", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CoordinateEntity> geometryCoordinates;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<CoordinateEntity> getGeometryCoordinates() {
        return geometryCoordinates;
    }

    public void setGeometryCoordinates(List<CoordinateEntity> geometryCoordinates) {
        this.geometryCoordinates = geometryCoordinates;
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

    public Long getObservationAreaId() {
        return observationAreaId;
    }

    public void setObservationAreaId(Long observationAreaId) {
        this.observationAreaId = observationAreaId;
    }
    
}