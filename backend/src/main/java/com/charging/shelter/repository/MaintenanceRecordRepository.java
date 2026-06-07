package com.charging.shelter.repository;

import com.charging.shelter.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    List<MaintenanceRecord> findBySocketId(Long socketId);

    List<MaintenanceRecord> findByShelterId(Long shelterId);

    List<MaintenanceRecord> findByOperatorId(Long operatorId);

    List<MaintenanceRecord> findByIsCompletedFalse();
}
