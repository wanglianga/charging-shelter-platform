package com.charging.shelter.entity;

import com.charging.shelter.enums.OrderStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "charging_orders")
public class ChargingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNo;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long socketId;

    private Long shelterId;

    private Long reservationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double startMeterReading;

    private Double endMeterReading;

    private Double chargedKwh;

    private Double unitPrice;

    private Double totalAmount;

    private Double baseFee;

    private Double penaltyFee;

    private LocalDateTime paidTime;

    private String remark;

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
}
