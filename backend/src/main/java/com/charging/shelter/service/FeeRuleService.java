package com.charging.shelter.service;

import com.charging.shelter.entity.FeeRule;
import com.charging.shelter.repository.FeeRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FeeRuleService {

    @Autowired
    private FeeRuleRepository feeRuleRepository;

    public List<FeeRule> findAll() {
        return feeRuleRepository.findAll();
    }

    public Optional<FeeRule> findById(Long id) {
        return feeRuleRepository.findById(id);
    }

    public Optional<FeeRule> findActive() {
        return feeRuleRepository.findByIsActiveTrue();
    }

    @Transactional
    public FeeRule save(FeeRule feeRule) {
        if (feeRule.getIsActive() != null && feeRule.getIsActive()) {
            feeRuleRepository.findAll().forEach(rule -> {
                if (!rule.getId().equals(feeRule.getId())) {
                    rule.setIsActive(false);
                    feeRuleRepository.save(rule);
                }
            });
        }
        return feeRuleRepository.save(feeRule);
    }

    public void deleteById(Long id) {
        feeRuleRepository.deleteById(id);
    }
}
