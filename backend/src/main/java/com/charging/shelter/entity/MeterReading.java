package com.charging.shelter.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meter_readings")
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long socketId;

    private Long shelterId;

    @Column(nullable = false)
    private Double reading;

    @Column(nullable = false)
    private LocalDateTime readingTime;

    private Long operatorId;

    private String remark;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSocketId() {
        return socketId;
    }

    public void setSocketId(Long socketId) {
        this.socketId = socketId;
    }

    public Long getShelterId() {
        return shelterId;
    }

    public void setShelterId(Long shelterId) {
        this.shelterId = shelterId;
    }

    public Double getReading() {
        return reading;
    }

    public void setReading(Double reading) {
        this.reading = reading;
    }

    public LocalDateTime getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(LocalDateTime readingTime) {
        this.readingTime = readingTime;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
