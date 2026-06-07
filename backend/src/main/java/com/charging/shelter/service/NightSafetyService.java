package com.charging.shelter.service;

import com.charging.shelter.dto.HighRiskOrderDTO;
import com.charging.shelter.dto.NightSafetySummaryDTO;
import com.charging.shelter.entity.*;
import com.charging.shelter.enums.AlarmType;
import com.charging.shelter.enums.BatteryType;
import com.charging.shelter.enums.OrderStatus;
import com.charging.shelter.enums.RiskLevel;
import com.charging.shelter.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NightSafetyService {

    private static final LocalTime NIGHT_START = LocalTime.of(22, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final double HIGH_TEMPERATURE_THRESHOLD = 50.0;
    private static final double MEDIUM_TEMPERATURE_THRESHOLD = 40.0;
    private static final long LONG_CHARGING_THRESHOLD_MINUTES = 480;
    private static final int HIGH_TRIP_COUNT_THRESHOLD = 3;
    private static final int MEDIUM_TRIP_COUNT_THRESHOLD = 1;

    @Autowired
    private ChargingOrderRepository orderRepository;

    @Autowired
    private ChargingSocketRepository socketRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean isNightTime(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        if (NIGHT_START.isBefore(NIGHT_END)) {
            return !time.isBefore(NIGHT_START) && !time.isAfter(NIGHT_END);
        } else {
            return !time.isBefore(NIGHT_START) || !time.isAfter(NIGHT_END);
        }
    }

    public NightSafetySummaryDTO getNightSafetySummary() {
        List<ChargingOrder> chargingOrders = orderRepository.findByStatus(OrderStatus.CHARGING);
        
        NightSafetySummaryDTO summary = new NightSafetySummaryDTO();
        summary.setTotalChargingOrders(chargingOrders.size());
        summary.setNightPeriodDescription("夜间高风险时段: 22:00 - 次日06:00");

        List<HighRiskOrderDTO> highRiskOrders = new ArrayList<>();
        int criticalCount = 0;
        int highCount = 0;
        int mediumCount = 0;
        int lowCount = 0;

        for (ChargingOrder order : chargingOrders) {
            HighRiskOrderDTO riskOrder = evaluateOrderRisk(order);
            if (riskOrder.getRiskLevel() == RiskLevel.CRITICAL) {
                criticalCount++;
                highRiskOrders.add(riskOrder);
            } else if (riskOrder.getRiskLevel() == RiskLevel.HIGH) {
                highCount++;
                highRiskOrders.add(riskOrder);
            } else if (riskOrder.getRiskLevel() == RiskLevel.MEDIUM) {
                mediumCount++;
                highRiskOrders.add(riskOrder);
            } else {
                lowCount++;
            }
        }

        highRiskOrders.sort(Comparator.comparing(HighRiskOrderDTO::getRiskScore).reversed());

        summary.setCriticalRiskCount(criticalCount);
        summary.setHighRiskCount(highCount);
        summary.setMediumRiskCount(mediumCount);
        summary.setLowRiskCount(lowCount);
        summary.setHighRiskOrders(highRiskOrders);

        return summary;
    }

    public List<HighRiskOrderDTO> getHighRiskOrders() {
        List<ChargingOrder> chargingOrders = orderRepository.findByStatus(OrderStatus.CHARGING);
        return chargingOrders.stream()
                .map(this::evaluateOrderRisk)
                .filter(order -> order.getRiskLevel() == RiskLevel.HIGH || order.getRiskLevel() == RiskLevel.CRITICAL)
                .sorted(Comparator.comparing(HighRiskOrderDTO::getRiskScore).reversed())
                .collect(Collectors.toList());
    }

    public HighRiskOrderDTO evaluateOrderRisk(ChargingOrder order) {
        HighRiskOrderDTO dto = new HighRiskOrderDTO();
        dto.setOrderId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setSocketId(order.getSocketId());
        dto.setShelterId(order.getShelterId());
        dto.setStartTime(order.getStartTime());
        dto.setBatteryType(order.getBatteryType() != null ? order.getBatteryType() : BatteryType.UNKNOWN);

        Optional<ChargingSocket> socketOpt = socketRepository.findById(order.getSocketId());
        if (socketOpt.isPresent()) {
            ChargingSocket socket = socketOpt.get();
            dto.setSocketCode(socket.getSocketCode());
            dto.setLocation(socket.getLocation());
            dto.setCurrentTemperature(socket.getCurrentTemperature() != null ? socket.getCurrentTemperature() : 25.0);
            dto.setTripCount(socket.getTripCount() != null ? socket.getTripCount() : 0);
            dto.setLastTripTime(socket.getLastTripTime());
        } else {
            dto.setSocketCode("未知");
            dto.setCurrentTemperature(25.0);
            dto.setTripCount(0);
        }

        Optional<Shelter> shelterOpt = shelterRepository.findById(order.getShelterId());
        if (shelterOpt.isPresent()) {
            dto.setShelterName(shelterOpt.get().getName());
        }

        if (order.getStartTime() != null) {
            long duration = ChronoUnit.MINUTES.between(order.getStartTime(), LocalDateTime.now());
            dto.setChargingDurationMinutes(duration);
        }

        List<Alarm> socketAlarms = alarmRepository.findBySocketIdOrderByCreatedAtDesc(order.getSocketId());
        if (!socketAlarms.isEmpty()) {
            Alarm lastAlarm = socketAlarms.get(0);
            dto.setLastAlarmDescription(lastAlarm.getDescription());
            dto.setLastAlarmTime(lastAlarm.getCreatedAt());
        }

        List<String> riskFactors = new ArrayList<>();
        int riskScore = 0;

        if (dto.getCurrentTemperature() >= HIGH_TEMPERATURE_THRESHOLD) {
            riskScore += 40;
            riskFactors.add("插座温度过高 (" + String.format("%.1f", dto.getCurrentTemperature()) + "°C)");
        } else if (dto.getCurrentTemperature() >= MEDIUM_TEMPERATURE_THRESHOLD) {
            riskScore += 20;
            riskFactors.add("插座温度偏高 (" + String.format("%.1f", dto.getCurrentTemperature()) + "°C)");
        }

        if (dto.getChargingDurationMinutes() != null && dto.getChargingDurationMinutes() >= LONG_CHARGING_THRESHOLD_MINUTES) {
            riskScore += 30;
            riskFactors.add("充电时间过长 (" + dto.getChargingDurationMinutes() / 60 + "小时)");
        } else if (dto.getChargingDurationMinutes() != null && dto.getChargingDurationMinutes() >= 240) {
            riskScore += 10;
            riskFactors.add("充电时间偏长 (" + dto.getChargingDurationMinutes() / 60 + "小时)");
        }

        if (dto.getBatteryType() == BatteryType.LITHIUM_ION || dto.getBatteryType() == BatteryType.LITHIUM_IRON_PHOSPHATE) {
            riskScore += 15;
            riskFactors.add("锂电池类型需重点关注");
        }

        if (dto.getTripCount() >= HIGH_TRIP_COUNT_THRESHOLD) {
            riskScore += 35;
            riskFactors.add("历史跳闸次数多 (" + dto.getTripCount() + "次)");
        } else if (dto.getTripCount() >= MEDIUM_TRIP_COUNT_THRESHOLD) {
            riskScore += 15;
            riskFactors.add("有历史跳闸记录 (" + dto.getTripCount() + "次)");
        }

        if (dto.getLastTripTime() != null && ChronoUnit.DAYS.between(dto.getLastTripTime(), LocalDateTime.now()) <= 7) {
            riskScore += 20;
            riskFactors.add("近期有跳闸记录");
        }

        if (dto.getLastAlarmTime() != null && ChronoUnit.DAYS.between(dto.getLastAlarmTime(), LocalDateTime.now()) <= 7) {
            riskScore += 15;
            riskFactors.add("近期有报警记录");
        }

        boolean isNight = isNightTime(LocalDateTime.now());
        if (isNight) {
            riskScore += 10;
            riskFactors.add("处于夜间高风险时段");
        }

        dto.setRiskScore(riskScore);
        dto.setRiskFactors(riskFactors);

        if (riskScore >= 80) {
            dto.setRiskLevel(RiskLevel.CRITICAL);
        } else if (riskScore >= 50) {
            dto.setRiskLevel(RiskLevel.HIGH);
        } else if (riskScore >= 25) {
            dto.setRiskLevel(RiskLevel.MEDIUM);
        } else {
            dto.setRiskLevel(RiskLevel.LOW);
        }

        return dto;
    }

    public List<HighRiskOrderDTO> getInspectionFocusList() {
        NightSafetySummaryDTO summary = getNightSafetySummary();
        return summary.getHighRiskOrders().stream()
                .filter(order -> order.getRiskLevel() == RiskLevel.HIGH || order.getRiskLevel() == RiskLevel.CRITICAL)
                .collect(Collectors.toList());
    }
}
