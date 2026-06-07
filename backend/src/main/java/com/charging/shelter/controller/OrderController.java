package com.charging.shelter.controller;

import com.charging.shelter.dto.Result;
import com.charging.shelter.entity.ChargingOrder;
import com.charging.shelter.enums.OrderStatus;
import com.charging.shelter.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<List<ChargingOrder>> findAll() {
        return Result.success(orderService.findAll());
    }

    @GetMapping("/{id}")
    public Result<ChargingOrder> findById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(Result::success)
                .orElse(Result.error("订单不存在"));
    }

    @GetMapping("/orderNo/{orderNo}")
    public Result<ChargingOrder> findByOrderNo(@PathVariable String orderNo) {
        return orderService.findByOrderNo(orderNo)
                .map(Result::success)
                .orElse(Result.error("订单不存在"));
    }

    @GetMapping("/user/{userId}")
    public Result<List<ChargingOrder>> findByUserId(@PathVariable Long userId) {
        return Result.success(orderService.findByUserId(userId));
    }

    @GetMapping("/shelter/{shelterId}")
    public Result<List<ChargingOrder>> findByShelterId(@PathVariable Long shelterId) {
        return Result.success(orderService.findByShelterId(shelterId));
    }

    @GetMapping("/status/{status}")
    public Result<List<ChargingOrder>> findByStatus(@PathVariable OrderStatus status) {
        return Result.success(orderService.findByStatus(status));
    }

    @PostMapping("/start")
    public Result<ChargingOrder> startCharging(@RequestBody StartChargingRequest request) {
        try {
            return Result.success(orderService.startCharging(
                    request.getUserId(),
                    request.getSocketId(),
                    request.getReservationId()
            ));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/stop")
    public Result<ChargingOrder> stopCharging(@PathVariable Long id) {
        try {
            return Result.success(orderService.stopCharging(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/pay")
    public Result<ChargingOrder> pay(@PathVariable Long id) {
        try {
            return Result.success(orderService.payOrder(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    public static class StartChargingRequest {
        private Long userId;
        private Long socketId;
        private Long reservationId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getSocketId() {
            return socketId;
        }

        public void setSocketId(Long socketId) {
            this.socketId = socketId;
        }

        public Long getReservationId() {
            return reservationId;
        }

        public void setReservationId(Long reservationId) {
            this.reservationId = reservationId;
        }
    }
}
