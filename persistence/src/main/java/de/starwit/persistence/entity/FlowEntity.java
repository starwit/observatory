package de.starwit.persistence.entity;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.starwit.persistence.enumeration.Direction;
import de.starwit.persistence.serializer.ZonedDateTimeDeserializer;
import de.starwit.persistence.serializer.ZonedDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Flow Entity class
 */
@Entity
@Table(name = "flow")
public class FlowEntity extends AbstractEntity<Long> {

    // entity fields
    @NotNull
    @Column(name = "parkingareaid", nullable = false)
    private Long parkingAreaId;


    @Column(name = "objectid")
    private String objectId;


    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private Direction direction;


    @Column(name="flowtime")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime flowTime;


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

    public ZonedDateTime getFlowTime() {
        return flowTime;
    }

    public void setFlowTime(ZonedDateTime flowTime) {
        this.flowTime = flowTime;
    }

    // entity relations getters and setters
    public ObjectClassEntity getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(ObjectClassEntity objectClass) {
        this.objectClass = objectClass;
    }

}
