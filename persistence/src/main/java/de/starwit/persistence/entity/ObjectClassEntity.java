package de.starwit.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;

import java.time.ZonedDateTime;
import de.starwit.persistence.serializer.ZonedDateTimeSerializer;
import de.starwit.persistence.serializer.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.CascadeType;

/**
 * ObjectClass Entity class
 */
@Entity
@Table(name = "objectclass")
public class ObjectClassEntity extends AbstractEntity<Long> {

    // entity fields
    @Column(name = "name")
    private String name;


    @Column(name = "classid")
    private Integer classId;


    // entity relations
    @JsonFilter("filterId")
    @OneToMany(mappedBy = "objectClass")
    private Set<FlowEntity> flow;

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
    public Set<FlowEntity> getFlow() {
        return flow;
    }

    public void setFlow(Set<FlowEntity> flow) {
        this.flow = flow;
    }

}
