package com.charging.shelter.entity;

import com.charging.shelter.enums.SocketStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sockets")
public class ChargingSocket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String socketCode;

    @Column(nullable = false)
    private Long shelterId;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocketStatus status;

    private Double powerRating;

    private String qrCode;

    private Double currentTemperature;

    private Integer tripCount;

    private LocalDateTime lastTripTime;

    private LocalDateTime lastMaintenanceTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSocketCode() {
        return socketCode;
    }

    public void setSocketCode(String socketCode) {
        this.socketCode = socketCode;
    }

    public Long getShelterId() {
        return shelterId;
    }

    public void setShelterId(Long shelterId) {
        this.shelterId = shelterId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public SocketStatus getStatus() {
        return status;
    }

    public void setStatus(SocketStatus status) {
        this.status = status;
    }

    public Double getPowerRating() {
        return powerRating;
    }

    public void setPowerRating(Double powerRating) {
        this.powerRating = powerRating;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(Double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public Integer getTripCount() {
        return tripCount;
    }

    public void setTripCount(Integer tripCount) {
        this.tripCount = tripCount;
    }

    public LocalDateTime getLastTripTime() {
        return lastTripTime;
    }

    public void setLastTripTime(LocalDateTime lastTripTime) {
        this.lastTripTime = lastTripTime;
    }

    public LocalDateTime getLastMaintenanceTime() {
        return lastMaintenanceTime;
    }

    public void setLastMaintenanceTime(LocalDateTime lastMaintenanceTime) {
        this.lastMaintenanceTime = lastMaintenanceTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
