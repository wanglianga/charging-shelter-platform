package com.charging.shelter.service;

import com.charging.shelter.entity.Alarm;
import com.charging.shelter.enums.AlarmStatus;
import com.charging.shelter.enums.AlarmType;
import com.charging.shelter.repository.AlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlarmService {

    @Autowired
    private AlarmRepository alarmRepository;

    public List<Alarm> findAll() {
        return alarmRepository.findAll();
    }

    public Optional<Alarm> findById(Long id) {
        return alarmRepository.findById(id);
    }

    public List<Alarm> findByStatus(AlarmStatus status) {
        return alarmRepository.findByStatus(status);
    }

    public List<Alarm> findByType(AlarmType type) {
        return alarmRepository.findByType(type);
    }

    public List<Alarm> findByShelterId(Long shelterId) {
        return alarmRepository.findByShelterId(shelterId);
    }

    public Alarm createAlarm(Alarm alarm) {
        alarm.setStatus(AlarmStatus.PENDING);
        return alarmRepository.save(alarm);
    }

    @Transactional
    public Alarm processAlarm(Long alarmId, Long handlerId) {
        Optional<Alarm> alarmOpt = alarmRepository.findById(alarmId);
        if (alarmOpt.isPresent()) {
            Alarm alarm = alarmOpt.get();
            alarm.setStatus(AlarmStatus.PROCESSING);
            alarm.setHandlerId(handlerId);
            return alarmRepository.save(alarm);
        }
        throw new RuntimeException("告警不存在");
    }

    @Transactional
    public Alarm resolveAlarm(Long alarmId, String handleResult) {
        Optional<Alarm> alarmOpt = alarmRepository.findById(alarmId);
        if (alarmOpt.isPresent()) {
            Alarm alarm = alarmOpt.get();
            alarm.setStatus(AlarmStatus.RESOLVED);
            alarm.setHandleResult(handleResult);
            alarm.setHandleTime(LocalDateTime.now());
            return alarmRepository.save(alarm);
        }
        throw new RuntimeException("告警不存在");
    }

    @Transactional
    public Alarm dismissAlarm(Long alarmId, String handleResult) {
        Optional<Alarm> alarmOpt = alarmRepository.findById(alarmId);
        if (alarmOpt.isPresent()) {
            Alarm alarm = alarmOpt.get();
            alarm.setStatus(AlarmStatus.DISMISSED);
            alarm.setHandleResult(handleResult);
            alarm.setHandleTime(LocalDateTime.now());
            return alarmRepository.save(alarm);
        }
        throw new RuntimeException("告警不存在");
    }
}
