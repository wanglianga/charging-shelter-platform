package com.charging.shelter.service;

import com.charging.shelter.entity.Complaint;
import com.charging.shelter.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    public List<Complaint> findAll() {
        return complaintRepository.findAll();
    }

    public Optional<Complaint> findById(Long id) {
        return complaintRepository.findById(id);
    }

    public List<Complaint> findByUserId(Long userId) {
        return complaintRepository.findByUserId(userId);
    }

    public List<Complaint> findByShelterId(Long shelterId) {
        return complaintRepository.findByShelterId(shelterId);
    }

    public List<Complaint> findByStatus(String status) {
        return complaintRepository.findByStatus(status);
    }

    public Complaint create(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint handle(Long complaintId, Long handlerId, String handleResult) {
        Optional<Complaint> complaintOpt = complaintRepository.findById(complaintId);
        if (complaintOpt.isPresent()) {
            Complaint complaint = complaintOpt.get();
            complaint.setStatus("RESOLVED");
            complaint.setHandlerId(handlerId);
            complaint.setHandleResult(handleResult);
            complaint.setHandleTime(LocalDateTime.now());
            return complaintRepository.save(complaint);
        }
        throw new RuntimeException("投诉不存在");
    }
}
