package com.charging.shelter.controller;

import com.charging.shelter.dto.Result;
import com.charging.shelter.entity.MaintenanceRecord;
import com.charging.shelter.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @GetMapping
    public Result<List<MaintenanceRecord>> findAll() {
        return Result.success(maintenanceService.findAll());
    }

    @GetMapping("/{id}")
    public Result<MaintenanceRecord> findById(@PathVariable Long id) {
        return maintenanceService.findById(id)
                .map(Result::success)
                .orElse(Result.error("维修记录不存在"));
    }

    @GetMapping("/socket/{socketId}")
    public Result<List<MaintenanceRecord>> findBySocketId(@PathVariable Long socketId) {
        return Result.success(maintenanceService.findBySocketId(socketId));
    }

    @GetMapping("/operator/{operatorId}")
    public Result<List<MaintenanceRecord>> findByOperatorId(@PathVariable Long operatorId) {
        return Result.success(maintenanceService.findByOperatorId(operatorId));
    }

    @GetMapping("/pending")
    public Result<List<MaintenanceRecord>> findPending() {
        return Result.success(maintenanceService.findPending());
    }

    @PostMapping
    public Result<MaintenanceRecord> create(@RequestBody MaintenanceRecord record) {
        return Result.success(maintenanceService.create(record));
    }

    @PutMapping("/{id}/complete")
    public Result<MaintenanceRecord> complete(@PathVariable Long id, @RequestBody CompleteRequest request) {
        try {
            return Result.success(maintenanceService.complete(id, request.getRemark()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    public static class CompleteRequest {
        private String remark;

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
