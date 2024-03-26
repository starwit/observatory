package de.starwit.persistence.analytics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ObjectClass Entity class
 */
@Entity
@Table(name = "objectclass")
public class ObjectClassEntity {

    // entity fields
    @Column(name = "name")
    private String name;

    @Id
    @Column(name = "class_id")
    private Long id;

    // entity fields getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
