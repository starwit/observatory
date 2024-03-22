package de.starwit.persistence.analytics.entity;

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

}