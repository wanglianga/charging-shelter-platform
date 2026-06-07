package com.charging.shelter.repository;

import com.charging.shelter.entity.Reservation;
import com.charging.shelter.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findBySocketId(Long socketId);

    List<Reservation> findByShelterId(Long shelterId);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    List<Reservation> findBySocketIdAndStatus(Long socketId, ReservationStatus status);

    List<Reservation> findByExpectedStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
