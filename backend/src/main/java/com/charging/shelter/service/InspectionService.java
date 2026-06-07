package com.charging.shelter.service;

import com.charging.shelter.entity.Inspection;
import com.charging.shelter.enums.InspectionType;
import com.charging.shelter.repository.InspectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InspectionService {

    @Autowired
    private InspectionRepository inspectionRepository;

    public List<Inspection> findAll() {
        return inspectionRepository.findAll();
    }

    public Optional<Inspection> findById(Long id) {
        return inspectionRepository.findById(id);
    }

    public List<Inspection> findByOfficerId(Long officerId) {
        return inspectionRepository.findByOfficerId(officerId);
    }

    public List<Inspection> findByShelterId(Long shelterId) {
        return inspectionRepository.findByShelterId(shelterId);
    }

    public List<Inspection> findByType(InspectionType type) {
        return inspectionRepository.findByType(type);
    }

    public List<Inspection> findHasIssues() {
        return inspectionRepository.findByHasIssueTrue();
    }

    public Inspection save(Inspection inspection) {
        if (inspection.getIsResolved() == null) {
            inspection.setIsResolved(false);
        }
        return inspectionRepository.save(inspection);
    }

    public Inspection resolveInspection(Long inspectionId, String handleSuggestion) {
        Optional<Inspection> inspectionOpt = inspectionRepository.findById(inspectionId);
        if (inspectionOpt.isPresent()) {
            Inspection inspection = inspectionOpt.get();
            inspection.setIsResolved(true);
            inspection.setHandleSuggestion(handleSuggestion);
            return inspectionRepository.save(inspection);
        }
        throw new RuntimeException("巡检记录不存在");
    }
}
