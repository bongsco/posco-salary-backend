package com.bongsco.api.adjust.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.adjust.common.entity.AdjustStep;

public interface AdjustStepRepository extends JpaRepository<AdjustStep, Long> {
}
