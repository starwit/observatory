package de.starwit.persistence.analytics.entity;

import java.math.BigDecimal;

import de.starwit.persistence.common.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "coordinate")
public class CoordinateEntity extends AbstractEntity<Long> {

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @ManyToOne
    private MetadataEntity metadata;

    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public MetadataEntity getMetadata() {
        return metadata;
    }
    
    public void setMetadata(MetadataEntity metadata) {
        this.metadata = metadata;
    }
    
}
