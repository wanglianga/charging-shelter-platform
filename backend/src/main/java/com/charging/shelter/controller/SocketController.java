package com.charging.shelter.controller;

import com.charging.shelter.dto.Result;
import com.charging.shelter.entity.ChargingSocket;
import com.charging.shelter.enums.SocketStatus;
import com.charging.shelter.service.SocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sockets")
public class SocketController {

    @Autowired
    private SocketService socketService;

    @GetMapping
    public Result<List<ChargingSocket>> findAll() {
        return Result.success(socketService.findAll());
    }

    @GetMapping("/{id}")
    public Result<ChargingSocket> findById(@PathVariable Long id) {
        return socketService.findById(id)
                .map(Result::success)
                .orElse(Result.error("插座不存在"));
    }

    @GetMapping("/shelter/{shelterId}")
    public Result<List<ChargingSocket>> findByShelterId(@PathVariable Long shelterId) {
        return Result.success(socketService.findByShelterId(shelterId));
    }

    @GetMapping("/shelter/{shelterId}/status/{status}")
    public Result<List<ChargingSocket>> findByShelterIdAndStatus(
            @PathVariable Long shelterId,
            @PathVariable SocketStatus status) {
        return Result.success(socketService.findByShelterIdAndStatus(shelterId, status));
    }

    @PostMapping
    public Result<ChargingSocket> save(@RequestBody ChargingSocket socket) {
        return Result.success(socketService.save(socket));
    }

    @PutMapping("/{id}")
    public Result<ChargingSocket> update(@PathVariable Long id, @RequestBody ChargingSocket socket) {
        socket.setId(id);
        return Result.success(socketService.save(socket));
    }

    @PutMapping("/{id}/status/{status}")
    public Result<ChargingSocket> updateStatus(
            @PathVariable Long id,
            @PathVariable SocketStatus status) {
        return Result.success(socketService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        socketService.deleteById(id);
        return Result.success();
    }
}
