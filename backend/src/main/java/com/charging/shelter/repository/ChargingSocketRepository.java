package com.charging.shelter.repository;

import com.charging.shelter.entity.ChargingSocket;
import com.charging.shelter.enums.SocketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingSocketRepository extends JpaRepository<ChargingSocket, Long> {

    List<ChargingSocket> findByShelterId(Long shelterId);

    List<ChargingSocket> findByShelterIdAndStatus(Long shelterId, SocketStatus status);

    List<ChargingSocket> findByStatus(SocketStatus status);
}
