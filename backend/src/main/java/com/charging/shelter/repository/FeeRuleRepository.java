package com.charging.shelter.repository;

import com.charging.shelter.entity.FeeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeeRuleRepository extends JpaRepository<FeeRule, Long> {

    Optional<FeeRule> findByIsActiveTrue();
}
