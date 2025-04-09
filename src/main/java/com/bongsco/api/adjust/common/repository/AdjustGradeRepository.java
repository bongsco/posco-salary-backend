package com.bongsco.api.adjust.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bongsco.api.adjust.common.entity.AdjustGrade;
public interface AdjustGradeRepository extends JpaRepository<AdjustGrade, Long> {
    @Query("""
            SELECT ag FROM AdjustGrade ag
            JOIN FETCH ag.grade
            WHERE ag.adjust.id = :adjustId
        """)
    List<AdjustGrade> findByAdjustId(@Param("adjustId") Long adjustId);

    List<AdjustGrade> findByAdjustIdAndIsActive(Long adjustId, boolean isActive);
}
