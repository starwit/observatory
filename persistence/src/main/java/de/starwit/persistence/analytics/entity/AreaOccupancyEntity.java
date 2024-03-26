package de.starwit.persistence.analytics.entity;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.starwit.persistence.common.serializer.ZonedDateTimeDeserializer;
import de.starwit.persistence.common.serializer.ZonedDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * AreaOccupancy Entity class
 */
@Entity
@Table(name = "areaoccupancy")
public class AreaOccupancyEntity {

    @Id
    @Column(name = "occupancy_time", nullable = false)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime occupancyTime;

    // entity fields
    @NotNull
    @Column(name = "observation_area_id", nullable = false)
    private Long observationAreaId;

    @NotNull
    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "object_class_id")
    private Integer objectClassId;

    @Column(name = "metadata_id")
    private Long metadataId;

    public Long getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(Long metadataId) {
        this.metadataId = metadataId;
    }

    // entity fields getters and setters
    public ZonedDateTime getOccupancyTime() {
        return occupancyTime;
    }

    public void setOccupancyTime(ZonedDateTime occupancyTime) {
        this.occupancyTime = occupancyTime;
    }

    public Long getObservationAreaId() {
        return observationAreaId;
    }

    public void setObservationAreaId(Long observationAreaId) {
        this.observationAreaId = observationAreaId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    // entity relations getters and setters
    public Integer getObjectClassId() {
        return objectClassId;
    }

    public void setObjectClassId(Integer objectClassId) {
        this.objectClassId = objectClassId;
    }

}
