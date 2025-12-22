package de.starwit.persistence.analytics.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LineCrossing Entity class
 */
@Entity
@Table(name = "linecrossing")
public class LineCrossingEntity {

    @Id
    @Column(name = "crossing_time")
    private ZonedDateTime crossingTime;

    // entity fields
    @Column(name = "object_id")
    private String objectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private Direction direction;

    // entity relations
    @Column(name = "object_class_id")
    private Long objectClassId;

    @Column(name = "metadata_id")
    private Long metadataId;

    public Long getObjectClassId() {
        return objectClassId;
    }

    public void setObjectClassId(Long objectClassId) {
        this.objectClassId = objectClassId;
    }

    public Long getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(Long metadata) {
        this.metadataId = metadata;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public ZonedDateTime getCrossingTime() {
        return crossingTime;
    }

    public void setCrossingTime(ZonedDateTime crossingTime) {
        this.crossingTime = crossingTime;
    }

}
