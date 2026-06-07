package com.charging.shelter.service;

import com.charging.shelter.entity.ChargingOrder;
import com.charging.shelter.entity.ChargingSocket;
import com.charging.shelter.entity.FeeRule;
import com.charging.shelter.entity.MeterReading;
import com.charging.shelter.entity.Reservation;
import com.charging.shelter.enums.OrderStatus;
import com.charging.shelter.enums.ReservationStatus;
import com.charging.shelter.enums.SocketStatus;
import com.charging.shelter.repository.ChargingOrderRepository;
import com.charging.shelter.repository.FeeRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private ChargingOrderRepository orderRepository;

    @Autowired
    private FeeRuleRepository feeRuleRepository;

    @Autowired
    private SocketService socketService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MeterReadingService meterReadingService;

    public List<ChargingOrder> findAll() {
        return orderRepository.findAll();
    }

    public Optional<ChargingOrder> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<ChargingOrder> findByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    public List<ChargingOrder> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<ChargingOrder> findByShelterId(Long shelterId) {
        return orderRepository.findByShelterId(shelterId);
    }

    public List<ChargingOrder> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public ChargingOrder startCharging(Long userId, Long socketId, Long reservationId) {
        Optional<ChargingSocket> socketOpt = socketService.findById(socketId);
        if (!socketOpt.isPresent()) {
            throw new RuntimeException("插座不存在");
        }

        ChargingSocket socket = socketOpt.get();
        if (socket.getStatus() != SocketStatus.OCCUPIED && socket.getStatus() != SocketStatus.RESERVED) {
            throw new RuntimeException("插座状态异常，无法开始充电");
        }

        FeeRule feeRule = feeRuleRepository.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("收费规则未配置"));

        List<MeterReading> readings = meterReadingService.findBySocketId(socketId);
        Double startReading = readings.isEmpty() ? 0.0 : readings.get(0).getReading();

        ChargingOrder order = new ChargingOrder();
        order.setOrderNo("ORD" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        order.setUserId(userId);
        order.setSocketId(socketId);
        order.setShelterId(socket.getShelterId());
        order.setReservationId(reservationId);
        order.setStatus(OrderStatus.CHARGING);
        order.setStartTime(LocalDateTime.now());
        order.setStartMeterReading(startReading);
        order.setUnitPrice(feeRule.getUnitPrice());
        order.setBaseFee(feeRule.getBaseFee());

        socketService.updateStatus(socketId, SocketStatus.CHARGING);

        return orderRepository.save(order);
    }

    @Transactional
    public ChargingOrder stopCharging(Long orderId) {
        Optional<ChargingOrder> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("订单不存在");
        }

        ChargingOrder order = orderOpt.get();
        if (order.getStatus() != OrderStatus.CHARGING) {
            throw new RuntimeException("订单状态异常");
        }

        LocalDateTime endTime = LocalDateTime.now();
        order.setEndTime(endTime);

        MeterReading reading = new MeterReading();
        reading.setSocketId(order.getSocketId());
        reading.setShelterId(order.getShelterId());
        reading.setReading(order.getStartMeterReading() + Math.random() * 10);
        reading.setReadingTime(endTime);
        meterReadingService.save(reading);

        order.setEndMeterReading(reading.getReading());
        order.setChargedKwh(order.getEndMeterReading() - order.getStartMeterReading());

        long minutes = ChronoUnit.MINUTES.between(order.getStartTime(), endTime);
        double amount = order.getChargedKwh() * order.getUnitPrice() + order.getBaseFee();

        FeeRule feeRule = feeRuleRepository.findByIsActiveTrue().orElse(null);
        if (feeRule != null && minutes > feeRule.getMaxChargingHours() * 60) {
            order.setPenaltyFee(feeRule.getOvertimePenalty());
            amount += order.getPenaltyFee();
            order.setStatus(OrderStatus.OVERTIME);
        } else {
            order.setStatus(OrderStatus.COMPLETED);
        }

        order.setTotalAmount(Math.round(amount * 100.0) / 100.0);

        socketService.updateStatus(order.getSocketId(), SocketStatus.AVAILABLE);

        if (order.getReservationId() != null) {
            reservationService.completeReservation(order.getReservationId());
        }

        return orderRepository.save(order);
    }

    @Transactional
    public ChargingOrder payOrder(Long orderId) {
        Optional<ChargingOrder> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("订单不存在");
        }

        ChargingOrder order = orderOpt.get();
        order.setPaidTime(LocalDateTime.now());
        return orderRepository.save(order);
    }
}
