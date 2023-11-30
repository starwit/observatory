package de.starwit.persistence.analytics.entity;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.starwit.persistence.common.entity.AbstractEntity;
import de.starwit.persistence.common.serializer.ZonedDateTimeDeserializer;
import de.starwit.persistence.common.serializer.ZonedDateTimeSerializer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * AreaOccupancy Entity class
 */
@Entity
@Table(name = "areaoccupancy")
public class AreaOccupancyEntity extends AbstractEntity<Long> {

    // entity fields
    @NotNull
    @Column(name = "occupancytime", nullable = false)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime occupancyTime;

    @NotNull
    @Column(name = "count", nullable = false)
    private Integer count;

    // entity relations
    @JsonFilter("filterId")
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "objectclass_id", referencedColumnName = "id", unique = true)
    private ObjectClassEntity objectClass;

    // entity fields getters and setters
    public ZonedDateTime getOccupancyTime() {
        return occupancyTime;
    }

    public void setOccupancyTime(ZonedDateTime occupancyTime) {
        this.occupancyTime = occupancyTime;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    // entity relations getters and setters
    public ObjectClassEntity getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(ObjectClassEntity objectClass) {
        this.objectClass = objectClass;
    }

}
