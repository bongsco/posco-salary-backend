package com.bongsco.api.adjust.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.common.domain.StepName;
import com.bongsco.api.adjust.common.entity.AdjustStep;

@Repository
public interface AdjustStepRepository extends JpaRepository<AdjustStep, Long> {
    List<AdjustStep> findByAdjustIdOrderByStep_OrderNumberAsc(Long adjustId);

    AdjustStep findByAdjust_IdAndStep_Id(Long adjustId, String stepId);

    @Modifying
    @Query(value = """
        UPDATE AdjustStep adjs
            SET adjs.isDone = false
        WHERE adjs.adjust.id = :adjustId
            AND adjs.step.name = :stepName
        """)
    void resetAdjustStepByAdjustIdAndStepName(
        @Param("adjustId") Long adjustId,
        @Param("stepName") StepName stepName
    );

    @Modifying
    @Query(value = """
        UPDATE AdjustStep adjs
            SET adjs.isDone = false
        WHERE adjs.adjust.id = :adjustId
        """)
    void resetAdjustStepByAdjustId(
        @Param("adjustId") Long adjustId);
}
