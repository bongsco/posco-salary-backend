package com.bongsco.api.adjust.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bongsco.api.adjust.common.entity.AdjustGrade;
public interface AdjustGradeRepository extends JpaRepository<AdjustGrade, Long> {
    List<AdjustGrade> findByAdjustId(Long adjustId);

    @Query("""
            SELECT a FROM AdjustGrade a
            WHERE a.adjust.id = :adjustId
              AND a.grade.id = :gradeId
        """)
    Optional<AdjustGrade> findByAdjustIdAndGradeId(@Param("adjustId") Long adjustId, @Param("gradeId") Long gradeId);
}
