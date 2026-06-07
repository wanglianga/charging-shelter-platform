package com.charging.shelter.repository;

import com.charging.shelter.entity.Alarm;
import com.charging.shelter.enums.AlarmStatus;
import com.charging.shelter.enums.AlarmType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByStatus(AlarmStatus status);

    List<Alarm> findByType(AlarmType type);

    List<Alarm> findByShelterId(Long shelterId);

    List<Alarm> findByHandlerId(Long handlerId);

    List<Alarm> findByTypeAndStatus(AlarmType type, AlarmStatus status);
}
