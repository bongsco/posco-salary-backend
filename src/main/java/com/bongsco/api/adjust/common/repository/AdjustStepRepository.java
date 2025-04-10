package com.bongsco.api.adjust.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.common.entity.AdjustStep;

@Repository
public interface AdjustStepRepository extends JpaRepository<AdjustStep, Long> {
    List<AdjustStep> findByAdjustIdOrderByStep_OrderNumberAsc(Long adjustId);

    AdjustStep findByAdjust_IdAndStep_Id(Long adjustId, String stepId);
}
