package com.charging.shelter.controller;

import com.charging.shelter.dto.HighRiskOrderDTO;
import com.charging.shelter.dto.NightSafetySummaryDTO;
import com.charging.shelter.dto.Result;
import com.charging.shelter.service.NightSafetyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/night-safety")
public class NightSafetyController {

    @Autowired
    private NightSafetyService nightSafetyService;

    @GetMapping("/summary")
    public Result<NightSafetySummaryDTO> getSummary() {
        try {
            return Result.success(nightSafetyService.getNightSafetySummary());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/high-risk-orders")
    public Result<List<HighRiskOrderDTO>> getHighRiskOrders() {
        try {
            return Result.success(nightSafetyService.getHighRiskOrders());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/inspection-focus")
    public Result<List<HighRiskOrderDTO>> getInspectionFocusList() {
        try {
            return Result.success(nightSafetyService.getInspectionFocusList());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/is-night-time")
    public Result<Boolean> isNightTime() {
        try {
            return Result.success(nightSafetyService.isNightTime(java.time.LocalDateTime.now()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
