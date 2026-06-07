package com.charging.shelter.repository;

import com.charging.shelter.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserId(Long userId);

    List<Complaint> findByShelterId(Long shelterId);

    List<Complaint> findByStatus(String status);

    List<Complaint> findByHandlerId(Long handlerId);
}
