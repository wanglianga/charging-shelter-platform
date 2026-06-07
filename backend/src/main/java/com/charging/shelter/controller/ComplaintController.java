package com.charging.shelter.controller;

import com.charging.shelter.dto.Result;
import com.charging.shelter.entity.Complaint;
import com.charging.shelter.service.ComplaintService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @GetMapping
    public Result<List<Complaint>> findAll() {
        return Result.success(complaintService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Complaint> findById(@PathVariable Long id) {
        return complaintService.findById(id)
                .map(Result::success)
                .orElse(Result.error("投诉不存在"));
    }

    @GetMapping("/user/{userId}")
    public Result<List<Complaint>> findByUserId(@PathVariable Long userId) {
        return Result.success(complaintService.findByUserId(userId));
    }

    @GetMapping("/shelter/{shelterId}")
        public Result<List<Complaint>> findByShelterId(@PathVariable Long shelterId) {
        return Result.success(complaintService.findByShelterId(shelterId));
    }

    @GetMapping("/status/{status}")
    public Result<List<Complaint>> findByStatus(@PathVariable String status) {
        return Result.success(complaintService.findByStatus(status));
    }

    @PostMapping
    public Result<Complaint> create(@RequestBody Complaint complaint) {
        return Result.success(complaintService.create(complaint));
    }

    @PutMapping("/{id}/handle")
    public Result<Complaint> handle(@PathVariable Long id, @RequestBody HandleRequest request) {
        try {
            return Result.success(complaintService.handle(id, request.getHandlerId(), request.getHandleResult()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Data
    public static class HandleRequest {
        private Long handlerId;
        private String handleResult;
    }
}
