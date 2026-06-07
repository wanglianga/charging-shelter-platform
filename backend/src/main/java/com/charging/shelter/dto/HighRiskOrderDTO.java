package com.charging.shelter.dto;

import com.charging.shelter.enums.BatteryType;
import com.charging.shelter.enums.RiskLevel;

import java.time.LocalDateTime;
import java.util.List;

public class HighRiskOrderDTO {

    private Long orderId;
    private String orderNo;
    private Long socketId;
    private String socketCode;
    private Long shelterId;
    private String shelterName;
    private String location;
    private Long userId;
    private BatteryType batteryType;
    private Double currentTemperature;
    private LocalDateTime startTime;
    private Long chargingDurationMinutes;
    private Integer tripCount;
    private LocalDateTime lastTripTime;
    private RiskLevel riskLevel;
    private Integer riskScore;
    private List<String> riskFactors;
    private String lastAlarmDescription;
    private LocalDateTime lastAlarmTime;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getSocketId() {
        return socketId;
    }

    public void setSocketId(Long socketId) {
        this.socketId = socketId;
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

    public String getShelterName() {
        return shelterName;
    }

    public void setShelterName(String shelterName) {
        this.shelterName = shelterName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BatteryType getBatteryType() {
        return batteryType;
    }

    public void setBatteryType(BatteryType batteryType) {
        this.batteryType = batteryType;
    }

    public Double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(Double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Long getChargingDurationMinutes() {
        return chargingDurationMinutes;
    }

    public void setChargingDurationMinutes(Long chargingDurationMinutes) {
        this.chargingDurationMinutes = chargingDurationMinutes;
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

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public List<String> getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(List<String> riskFactors) {
        this.riskFactors = riskFactors;
    }

    public String getLastAlarmDescription() {
        return lastAlarmDescription;
    }

    public void setLastAlarmDescription(String lastAlarmDescription) {
        this.lastAlarmDescription = lastAlarmDescription;
    }

    public LocalDateTime getLastAlarmTime() {
        return lastAlarmTime;
    }

    public void setLastAlarmTime(LocalDateTime lastAlarmTime) {
        this.lastAlarmTime = lastAlarmTime;
    }
}
