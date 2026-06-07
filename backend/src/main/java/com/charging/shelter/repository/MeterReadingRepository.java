package com.charging.shelter.repository;

import com.charging.shelter.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

    List<MeterReading> findBySocketIdOrderByReadingTimeDesc(Long socketId);

    List<MeterReading> findByShelterId(Long shelterId);

    List<MeterReading> findBySocketIdAndReadingTimeBetween(Long socketId, LocalDateTime start, LocalDateTime end);
}
