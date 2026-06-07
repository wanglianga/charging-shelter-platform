package com.charging.shelter.service;

import com.charging.shelter.entity.ChargingSocket;
import com.charging.shelter.enums.SocketStatus;
import com.charging.shelter.repository.ChargingSocketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SocketService {

    @Autowired
    private ChargingSocketRepository socketRepository;

    public List<ChargingSocket> findAll() {
        return socketRepository.findAll();
    }

    public Optional<ChargingSocket> findById(Long id) {
        return socketRepository.findById(id);
    }

    public List<ChargingSocket> findByShelterId(Long shelterId) {
        return socketRepository.findByShelterId(shelterId);
    }

    public List<ChargingSocket> findByShelterIdAndStatus(Long shelterId, SocketStatus status) {
        return socketRepository.findByShelterIdAndStatus(shelterId, status);
    }

    public ChargingSocket save(ChargingSocket socket) {
        return socketRepository.save(socket);
    }

    public void deleteById(Long id) {
        socketRepository.deleteById(id);
    }

    public ChargingSocket updateStatus(Long socketId, SocketStatus status) {
        Optional<ChargingSocket> socketOpt = socketRepository.findById(socketId);
        if (socketOpt.isPresent()) {
            ChargingSocket socket = socketOpt.get();
            socket.setStatus(status);
            return socketRepository.save(socket);
        }
        return null;
    }

    public ChargingSocket updateMaintenanceTime(Long socketId) {
        Optional<ChargingSocket> socketOpt = socketRepository.findById(socketId);
        if (socketOpt.isPresent()) {
            ChargingSocket socket = socketOpt.get();
            socket.setLastMaintenanceTime(LocalDateTime.now());
            return socketRepository.save(socket);
        }
        return null;
    }
}
