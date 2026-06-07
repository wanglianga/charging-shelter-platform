package com.charging.shelter.controller;

import com.charging.shelter.dto.Result;
import com.charging.shelter.entity.Reservation;
import com.charging.shelter.enums.ReservationStatus;
import com.charging.shelter.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public Result<List<Reservation>> findAll() {
        return Result.success(reservationService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Reservation> findById(@PathVariable Long id) {
        return reservationService.findById(id)
                .map(Result::success)
                .orElse(Result.error("预约不存在"));
    }

    @GetMapping("/user/{userId}")
    public Result<List<Reservation>> findByUserId(@PathVariable Long userId) {
        return Result.success(reservationService.findByUserId(userId));
    }

    @GetMapping("/shelter/{shelterId}")
    public Result<List<Reservation>> findByShelterId(@PathVariable Long shelterId) {
        return Result.success(reservationService.findByShelterId(shelterId));
    }

    @GetMapping("/status/{status}")
    public Result<List<Reservation>> findByStatus(@PathVariable ReservationStatus status) {
        return Result.success(reservationService.findByStatus(status));
    }

    @PostMapping
    public Result<Reservation> create(@RequestBody Reservation reservation) {
        try {
            return Result.success(reservationService.createReservation(reservation));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/checkin")
    public Result<Reservation> checkIn(@PathVariable Long id) {
        try {
            return Result.success(reservationService.checkIn(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public Result<Reservation> cancel(@PathVariable Long id) {
        try {
            return Result.success(reservationService.cancelReservation(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/complete")
    public Result<Reservation> complete(@PathVariable Long id) {
        try {
            return Result.success(reservationService.completeReservation(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
