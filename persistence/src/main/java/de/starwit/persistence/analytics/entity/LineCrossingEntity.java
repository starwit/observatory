package de.starwit.persistence.analytics.entity;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.starwit.persistence.common.entity.AbstractEntity;
import de.starwit.persistence.common.serializer.ZonedDateTimeDeserializer;
import de.starwit.persistence.common.serializer.ZonedDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * LineCrossing Entity class
 */
@Entity
@Table(name = "linecrossing")
public class LineCrossingEntity extends AbstractEntity<Long> {

    // entity fields
    @NotNull
    @Column(name = "parkingareaid", nullable = false)
    private Long parkingAreaId;

    @Column(name = "objectid")
    private String objectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private Direction direction;

    @Column(name = "crossingtime")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime crossingTime;

    // entity relations
    @JsonFilter("filterId")
    @ManyToOne
    @JoinColumn(name = "objectclass_id")
    private ObjectClassEntity objectClass;

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

    // entity relations getters and setters
    public ObjectClassEntity getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(ObjectClassEntity objectClass) {
        this.objectClass = objectClass;
    }

}
