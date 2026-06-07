package com.charging.shelter.dto;

import java.util.List;

public class NightSafetySummaryDTO {

    private Integer totalChargingOrders;
    private Integer highRiskCount;
    private Integer mediumRiskCount;
    private Integer lowRiskCount;
    private Integer criticalRiskCount;
    private List<HighRiskOrderDTO> highRiskOrders;
    private String nightPeriodDescription;

    public Integer getTotalChargingOrders() {
        return totalChargingOrders;
    }

    public void setTotalChargingOrders(Integer totalChargingOrders) {
        this.totalChargingOrders = totalChargingOrders;
    }

    public Integer getHighRiskCount() {
        return highRiskCount;
    }

    public void setHighRiskCount(Integer highRiskCount) {
        this.highRiskCount = highRiskCount;
    }

    public Integer getMediumRiskCount() {
        return mediumRiskCount;
    }

    public void setMediumRiskCount(Integer mediumRiskCount) {
        this.mediumRiskCount = mediumRiskCount;
    }

    public Integer getLowRiskCount() {
        return lowRiskCount;
    }

    public void setLowRiskCount(Integer lowRiskCount) {
        this.lowRiskCount = lowRiskCount;
    }

    public Integer getCriticalRiskCount() {
        return criticalRiskCount;
    }

    public void setCriticalRiskCount(Integer criticalRiskCount) {
        this.criticalRiskCount = criticalRiskCount;
    }

    public List<HighRiskOrderDTO> getHighRiskOrders() {
        return highRiskOrders;
    }

    public void setHighRiskOrders(List<HighRiskOrderDTO> highRiskOrders) {
        this.highRiskOrders = highRiskOrders;
    }

    public String getNightPeriodDescription() {
        return nightPeriodDescription;
    }

    public void setNightPeriodDescription(String nightPeriodDescription) {
        this.nightPeriodDescription = nightPeriodDescription;
    }
}
