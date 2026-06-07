package com.charging.shelter.repository;

import com.charging.shelter.entity.Inspection;
import com.charging.shelter.enums.InspectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    List<Inspection> findByOfficerId(Long officerId);

    List<Inspection> findByShelterId(Long shelterId);

    List<Inspection> findByType(InspectionType type);

    List<Inspection> findByHasIssueTrue();

    List<Inspection> findByInspectionTimeBetween(LocalDateTime start, LocalDateTime end);
}
