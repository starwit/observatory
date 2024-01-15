package de.starwit.persistence.analytics.entity;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.starwit.persistence.common.serializer.ZonedDateTimeDeserializer;
import de.starwit.persistence.common.serializer.ZonedDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * LineCrossing Entity class
 */
@Entity
@Table(name = "linecrossing")
public class LineCrossingEntity {

    @Id
    @Column(name = "crossingtime")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime crossingTime;

    // entity fields
    @NotNull
    @Column(name = "parkingareaid", nullable = false)
    private Long parkingAreaId;

    @Column(name = "objectid")
    private String objectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private Direction direction;

    // entity relations
    @Column(name = "objectclassid")
    private Integer objectClassId;

    @Column(name = "metadataid")
    private Long metadataId;

    public Long getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(Long metadata) {
        this.metadataId = metadata;
    }

    // entity fields getters and setters
    public Long getParkingAreaId() {
        return parkingAreaId;
    }

    public void setParkingAreaId(Long parkingAreaId) {
        this.parkingAreaId = parkingAreaId;
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

    public Integer getObjectClassId() {
        return objectClassId;
    }

    public void setObjectClassId(Integer objectClassId) {
        this.objectClassId = objectClassId;
    }

}
