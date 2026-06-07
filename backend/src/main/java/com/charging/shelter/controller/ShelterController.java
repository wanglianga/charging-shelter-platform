package com.charging.shelter.controller;

import com.charging.shelter.dto.Result;
import com.charging.shelter.entity.Shelter;
import com.charging.shelter.service.ShelterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shelters")
public class ShelterController {

    @Autowired
    private ShelterService shelterService;

    @GetMapping
    public Result<List<Shelter>> findAll() {
        return Result.success(shelterService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Shelter> findById(@PathVariable Long id) {
        return shelterService.findById(id)
                .map(Result::success)
                .orElse(Result.error("车棚不存在"));
    }

    @PostMapping
    public Result<Shelter> save(@RequestBody Shelter shelter) {
        return Result.success(shelterService.save(shelter));
    }

    @PutMapping("/{id}")
    public Result<Shelter> update(@PathVariable Long id, @RequestBody Shelter shelter) {
        shelter.setId(id);
        return Result.success(shelterService.save(shelter));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        shelterService.deleteById(id);
        return Result.success();
    }
}
