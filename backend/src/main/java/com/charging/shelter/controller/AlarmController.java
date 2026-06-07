package com.charging.shelter.controller;

import com.charging.shelter.dto.Result;
import com.charging.shelter.entity.Alarm;
import com.charging.shelter.enums.AlarmStatus;
import com.charging.shelter.enums.AlarmType;
import com.charging.shelter.service.AlarmService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alarms")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @GetMapping
    public Result<List<Alarm>> findAll() {
        return Result.success(alarmService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Alarm> findById(@PathVariable Long id) {
        return alarmService.findById(id)
                .map(Result::success)
                .orElse(Result.error("告警不存在"));
    }

    @GetMapping("/status/{status}")
    public Result<List<Alarm>> findByStatus(@PathVariable AlarmStatus status) {
        return Result.success(alarmService.findByStatus(status));
    }

    @GetMapping("/type/{type}")
    public Result<List<Alarm>> findByType(@PathVariable AlarmType type) {
        return Result.success(alarmService.findByType(type));
    }

    @GetMapping("/shelter/{shelterId}")
    public Result<List<Alarm>> findByShelterId(@PathVariable Long shelterId) {
        return Result.success(alarmService.findByShelterId(shelterId));
    }

    @PostMapping
    public Result<Alarm> create(@RequestBody Alarm alarm) {
        return Result.success(alarmService.createAlarm(alarm));
    }

    @PutMapping("/{id}/process")
    public Result<Alarm> process(@PathVariable Long id, @RequestBody ProcessRequest request) {
        try {
            return Result.success(alarmService.processAlarm(id, request.getHandlerId()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/resolve")
    public Result<Alarm> resolve(@PathVariable Long id, @RequestBody HandleRequest request) {
        try {
            return Result.success(alarmService.resolveAlarm(id, request.getHandleResult()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/dismiss")
    public Result<Alarm> dismiss(@PathVariable Long id, @RequestBody HandleRequest request) {
        try {
            return Result.success(alarmService.dismissAlarm(id, request.getHandleResult()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Data
    public static class ProcessRequest {
        private Long handlerId;
    }

    @Data
    public static class HandleRequest {
        private String handleResult;
    }
}
