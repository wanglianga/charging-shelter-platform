package com.charging.shelter.repository;

import com.charging.shelter.entity.ChargingOrder;
import com.charging.shelter.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChargingOrderRepository extends JpaRepository<ChargingOrder, Long> {

    Optional<ChargingOrder> findByOrderNo(String orderNo);

    List<ChargingOrder> findByUserId(Long userId);

    List<ChargingOrder> findBySocketId(Long socketId);

    List<ChargingOrder> findByShelterId(Long shelterId);

    List<ChargingOrder> findByStatus(OrderStatus status);

    List<ChargingOrder> findByUserIdAndStatus(Long userId, OrderStatus status);

    List<ChargingOrder> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
