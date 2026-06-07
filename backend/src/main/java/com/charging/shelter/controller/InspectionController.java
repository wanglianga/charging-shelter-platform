package com.charging.shelter.controller;

import com.charging.shelter.dto.Result;
import com.charging.shelter.entity.Inspection;
import com.charging.shelter.enums.InspectionType;
import com.charging.shelter.service.InspectionService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inspections")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    @GetMapping
    public Result<List<Inspection>> findAll() {
        return Result.success(inspectionService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Inspection> findById(@PathVariable Long id) {
        return inspectionService.findById(id)
                .map(Result::success)
                .orElse(Result.error("巡检记录不存在"));
    }

    @GetMapping("/officer/{officerId}")
    public Result<List<Inspection>> findByOfficerId(@PathVariable Long officerId) {
        return Result.success(inspectionService.findByOfficerId(officerId));
    }

    @GetMapping("/shelter/{shelterId}")
    public Result<List<Inspection>> findByShelterId(@PathVariable Long shelterId) {
        return Result.success(inspectionService.findByShelterId(shelterId));
    }

    @GetMapping("/type/{type}")
    public Result<List<Inspection>> findByType(@PathVariable InspectionType type) {
        return Result.success(inspectionService.findByType(type));
    }

    @GetMapping("/issues")
    public Result<List<Inspection>> findHasIssues() {
        return Result.success(inspectionService.findHasIssues());
    }

    @PostMapping
    public Result<Inspection> save(@RequestBody Inspection inspection) {
        return Result.success(inspectionService.save(inspection));
    }

    @PutMapping("/{id}/resolve")
    public Result<Inspection> resolve(@PathVariable Long id, @RequestBody ResolveRequest request) {
        try {
            return Result.success(inspectionService.resolveInspection(id, request.getHandleSuggestion()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Data
    public static class ResolveRequest {
        private String handleSuggestion;
    }
}
