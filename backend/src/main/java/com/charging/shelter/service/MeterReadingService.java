package com.charging.shelter.service;

import com.charging.shelter.entity.MeterReading;
import com.charging.shelter.repository.MeterReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeterReadingService {

    @Autowired
    private MeterReadingRepository meterReadingRepository;

    public List<MeterReading> findAll() {
        return meterReadingRepository.findAll();
    }

    public Optional<MeterReading> findById(Long id) {
        return meterReadingRepository.findById(id);
    }

    public List<MeterReading> findBySocketId(Long socketId) {
        return meterReadingRepository.findBySocketIdOrderByReadingTimeDesc(socketId);
    }

    public List<MeterReading> findByShelterId(Long shelterId) {
        return meterReadingRepository.findByShelterId(shelterId);
    }

    public MeterReading save(MeterReading reading) {
        return meterReadingRepository.save(reading);
    }
}
