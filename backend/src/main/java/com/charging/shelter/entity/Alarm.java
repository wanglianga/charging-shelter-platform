package com.charging.shelter.entity;

import com.charging.shelter.enums.AlarmType;
import com.charging.shelter.enums.AlarmStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alarms")
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmStatus status;

    private Long shelterId;

    private Long socketId;

    private Long reporterId;

    private String description;

    private String location;

    private String imageUrl;

    private Long handlerId;

    private String handleResult;

    private LocalDateTime handleTime;

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

    public AlarmType getType() {
        return type;
    }

    public void setType(AlarmType type) {
        this.type = type;
    }

    public AlarmStatus getStatus() {
        return status;
    }

    public void setStatus(AlarmStatus status) {
        this.status = status;
    }

    public Long getShelterId() {
        return shelterId;
    }

    public void setShelterId(Long shelterId) {
        this.shelterId = shelterId;
    }

    public Long getSocketId() {
        return socketId;
    }

    public void setSocketId(Long socketId) {
        this.socketId = socketId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(Long handlerId) {
        this.handlerId = handlerId;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    public LocalDateTime getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(LocalDateTime handleTime) {
        this.handleTime = handleTime;
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
