package com.charging.shelter.controller;

import com.charging.shelter.dto.Result;
import com.charging.shelter.entity.MeterReading;
import com.charging.shelter.service.MeterReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meter-readings")
public class MeterReadingController {

    @Autowired
    private MeterReadingService meterReadingService;

    @GetMapping
    public Result<List<MeterReading>> findAll() {
        return Result.success(meterReadingService.findAll());
    }

    @GetMapping("/{id}")
    public Result<MeterReading> findById(@PathVariable Long id) {
        return meterReadingService.findById(id)
                .map(Result::success)
                .orElse(Result.error("电表读数不存在"));
    }

    @GetMapping("/socket/{socketId}")
    public Result<List<MeterReading>> findBySocketId(@PathVariable Long socketId) {
        return Result.success(meterReadingService.findBySocketId(socketId));
    }

    @GetMapping("/shelter/{shelterId}")
    public Result<List<MeterReading>> findByShelterId(@PathVariable Long shelterId) {
        return Result.success(meterReadingService.findByShelterId(shelterId));
    }

    @PostMapping
    public Result<MeterReading> save(@RequestBody MeterReading reading) {
        return Result.success(meterReadingService.save(reading));
    }
}
