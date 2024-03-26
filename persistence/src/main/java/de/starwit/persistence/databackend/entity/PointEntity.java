package de.starwit.persistence.databackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.starwit.persistence.common.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "point")
public class PointEntity extends AbstractEntity<Long> {

    @Column(name = "x")
    private BigDecimal x;

    @Column(name = "y")
    private BigDecimal y;

    @Column(name = "order_idx")
    private int orderIdx;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "observation_job_id")
    private ObservationJobEntity observationJob;

    public BigDecimal getX() {
        return x;
    }

    public void setX(BigDecimal x) {
        this.x = x;
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }

    public int getOrderIdx() {
        return orderIdx;
    }

    public void setOrderIdx(int orderIdx) {
        this.orderIdx = orderIdx;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public ObservationJobEntity getObservationJob() {
        return observationJob;
    }

    public void setObservationJob(ObservationJobEntity observationJob) {
        this.observationJob = observationJob;
    }

}