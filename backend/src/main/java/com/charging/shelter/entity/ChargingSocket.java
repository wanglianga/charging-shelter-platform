package com.charging.shelter.entity;

import com.charging.shelter.enums.SocketStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
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
}
