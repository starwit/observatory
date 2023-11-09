package de.starwit.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "point")
public class PointEntity extends AbstractEntity<Long> {
    
    @Column(name = "x")
    private Double x;
    
    @Column(name = "y")
    private Double y;

    @Column(name = "order_idx")
    private int orderIdx;

    @ManyToOne
    @JsonIgnore
    private AnalyticsJobEntity analyticsJob;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }
    
    public void setY(Double y) {
        this.y = y;
    }

    public int getOrderIdx() {
        return orderIdx;
    }

    public void setOrderIdx(int orderIdx) {
        this.orderIdx = orderIdx;
    }

    public AnalyticsJobEntity getAnalyticsJob() {
        return analyticsJob;
    }

    public void setAnalyticsJob(AnalyticsJobEntity analyticsJob) {
        this.analyticsJob = analyticsJob;
    }

}