package de.starwit.persistence.sae.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import de.starwit.persistence.common.entity.AbstractCaptureEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SaeCountEntity implements AbstractCaptureEntity {

    @Id
    @Column(name = "capture_ts")
    private Instant captureTs;

    @Column(name = "count")
    private Integer count;

    @Column(name = "class_id")
    private Integer objectClassId;

    public Integer getCount() {
        return count;
    }

    public Instant getCaptureTs() {
        return captureTs;
    }

    public void setCaptureTs(Instant captureTs) {
        this.captureTs = captureTs;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getObjectClassId() {
        return objectClassId;
    }

    public void setObjectClassId(Integer objectClassId) {
        this.objectClassId = objectClassId;
    }

    public static SaeCountEntity from(ResultSet rs) throws SQLException {
        SaeCountEntity dto = new SaeCountEntity();
        dto.setCaptureTs(rs.getTimestamp("capture_ts").toInstant());
        dto.setObjectClassId(rs.getInt("class_id"));
        dto.setCount(rs.getInt("count"));
        return dto;
    }

}
