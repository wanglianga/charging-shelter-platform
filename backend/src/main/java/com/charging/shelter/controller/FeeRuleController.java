package com.charging.shelter.controller;

import com.charging.shelter.dto.Result;
import com.charging.shelter.entity.FeeRule;
import com.charging.shelter.service.FeeRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fee-rules")
public class FeeRuleController {

    @Autowired
    private FeeRuleService feeRuleService;

    @GetMapping
    public Result<List<FeeRule>> findAll() {
        return Result.success(feeRuleService.findAll());
    }

    @GetMapping("/{id}")
    public Result<FeeRule> findById(@PathVariable Long id) {
        return feeRuleService.findById(id)
                .map(Result::success)
                .orElse(Result.error("收费规则不存在"));
    }

    @GetMapping("/active")
    public Result<FeeRule> findActive() {
        return feeRuleService.findActive()
                .map(Result::success)
                .orElse(Result.error("无激活的收费规则"));
    }

    @PostMapping
    public Result<FeeRule> save(@RequestBody FeeRule feeRule) {
        return Result.success(feeRuleService.save(feeRule));
    }

    @PutMapping("/{id}")
    public Result<FeeRule> update(@PathVariable Long id, @RequestBody FeeRule feeRule) {
        feeRule.setId(id);
        return Result.success(feeRuleService.save(feeRule));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        feeRuleService.deleteById(id);
        return Result.success();
    }
}
