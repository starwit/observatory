package de.starwit.persistence.analytics.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;

import de.starwit.persistence.common.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * ObjectClass Entity class
 */
@Entity
@Table(name = "objectclass")
public class ObjectClassEntity extends AbstractEntity<Long> {

    // entity fields
    @Column(name = "name")
    private String name;

    @Column(name = "classid", unique = true)
    private Integer classId;

    // entity relations
    @JsonFilter("filterId")
    @OneToMany(mappedBy = "objectClass")
    private Set<LineCrossingEntity> flow;

    @OneToOne(mappedBy = "objectClass")
    private AreaOccupancyEntity areaOccupancy;

    // entity fields getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    // entity relations getters and setters
    public Set<LineCrossingEntity> getFlow() {
        return flow;
    }

    public void setFlow(Set<LineCrossingEntity> flow) {
        this.flow = flow;
    }

    public AreaOccupancyEntity getAreaOccupancy() {
        return areaOccupancy;
    }

    public void setAreaOccupancy(AreaOccupancyEntity areaOccupancy) {
        this.areaOccupancy = areaOccupancy;
    }

}
