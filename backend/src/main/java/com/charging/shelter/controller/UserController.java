package com.charging.shelter.controller;

import com.charging.shelter.dto.Result;
import com.charging.shelter.entity.User;
import com.charging.shelter.enums.UserRole;
import com.charging.shelter.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<List<User>> findAll() {
        return Result.success(userService.findAll());
    }

    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id) {
        return userService.findById(id)
                .map(Result::success)
                .orElse(Result.error("用户不存在"));
    }

    @GetMapping("/username/{username}")
    public Result<User> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(Result::success)
                .orElse(Result.error("用户不存在"));
    }

    @GetMapping("/role/{role}")
    public Result<List<User>> findByRole(@PathVariable UserRole role) {
        return Result.success(userService.findByRole(role));
    }

    @GetMapping("/blacklist")
    public Result<List<User>> findBlacklisted() {
        return Result.success(userService.findBlacklisted());
    }

    @PostMapping
    public Result<User> save(@RequestBody User user) {
        return Result.success(userService.save(user));
    }

    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return Result.success(userService.save(user));
    }

    @PutMapping("/{id}/blacklist")
    public Result<User> setBlacklist(@PathVariable Long id, @RequestBody BlacklistRequest request) {
        try {
            return Result.success(userService.setBlacklist(id, request.getIsBlacklisted()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        return userService.findByUsername(request.getUsername())
                .filter(user -> user.getPassword().equals(request.getPassword()))
                .map(Result::success)
                .orElse(Result.error("用户名或密码错误"));
    }

    @Data
    public static class BlacklistRequest {
        private Boolean isBlacklisted;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
