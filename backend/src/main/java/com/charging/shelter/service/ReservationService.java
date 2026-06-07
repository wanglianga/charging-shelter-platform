package com.charging.shelter.service;

import com.charging.shelter.entity.ChargingSocket;
import com.charging.shelter.entity.Reservation;
import com.charging.shelter.enums.ReservationStatus;
import com.charging.shelter.enums.SocketStatus;
import com.charging.shelter.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SocketService socketService;

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> findByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> findByShelterId(Long shelterId) {
        return reservationRepository.findByShelterId(shelterId);
    }

    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        Optional<ChargingSocket> socketOpt = socketService.findById(reservation.getSocketId());
        if (socketOpt.isPresent()) {
            ChargingSocket socket = socketOpt.get();
            if (socket.getStatus() == SocketStatus.AVAILABLE) {
                socketService.updateStatus(reservation.getSocketId(), SocketStatus.RESERVED);
                reservation.setStatus(ReservationStatus.CONFIRMED);
                reservation.setShelterId(socket.getShelterId());
                return reservationRepository.save(reservation);
            }
        }
        throw new RuntimeException("插座不可预约");
    }

    @Transactional
    public Reservation checkIn(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                reservation.setStatus(ReservationStatus.CHECKED_IN);
                reservation.setCheckInTime(LocalDateTime.now());
                socketService.updateStatus(reservation.getSocketId(), SocketStatus.OCCUPIED);
                return reservationRepository.save(reservation);
            }
        }
        throw new RuntimeException("预约状态异常");
    }

    @Transactional
    public Reservation cancelReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservation.setCancelTime(LocalDateTime.now());
                socketService.updateStatus(reservation.getSocketId(), SocketStatus.AVAILABLE);
                return reservationRepository.save(reservation);
            }
        }
        throw new RuntimeException("预约状态异常");
    }

    @Transactional
    public Reservation completeReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatus(ReservationStatus.COMPLETED);
            socketService.updateStatus(reservation.getSocketId(), SocketStatus.AVAILABLE);
            return reservationRepository.save(reservation);
        }
        throw new RuntimeException("预约不存在");
    }
}
