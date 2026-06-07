package com.charging.shelter.service;

import com.charging.shelter.entity.MaintenanceRecord;
import com.charging.shelter.repository.MaintenanceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRecordRepository maintenanceRepository;

    @Autowired
    private SocketService socketService;

    public List<MaintenanceRecord> findAll() {
        return maintenanceRepository.findAll();
    }

    public Optional<MaintenanceRecord> findById(Long id) {
        return maintenanceRepository.findById(id);
    }

    public List<MaintenanceRecord> findBySocketId(Long socketId) {
        return maintenanceRepository.findBySocketId(socketId);
    }

    public List<MaintenanceRecord> findByOperatorId(Long operatorId) {
        return maintenanceRepository.findByOperatorId(operatorId);
    }

    public List<MaintenanceRecord> findPending() {
        return maintenanceRepository.findByIsCompletedFalse();
    }

    public MaintenanceRecord create(MaintenanceRecord record) {
        record.setIsCompleted(false);
        record.setStartTime(LocalDateTime.now());
        return maintenanceRepository.save(record);
    }

    @Transactional
    public MaintenanceRecord complete(Long recordId, String remark) {
        Optional<MaintenanceRecord> recordOpt = maintenanceRepository.findById(recordId);
        if (recordOpt.isPresent()) {
            MaintenanceRecord record = recordOpt.get();
            record.setIsCompleted(true);
            record.setEndTime(LocalDateTime.now());
            record.setRemark(remark);
            socketService.updateMaintenanceTime(record.getSocketId());
            return maintenanceRepository.save(record);
        }
        throw new RuntimeException("维修记录不存在");
    }
}
