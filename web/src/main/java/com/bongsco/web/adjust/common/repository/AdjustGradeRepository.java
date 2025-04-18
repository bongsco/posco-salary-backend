package com.bongsco.web.adjust.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bongsco.web.adjust.common.entity.AdjustGrade;
public interface AdjustGradeRepository extends JpaRepository<AdjustGrade, Long> {
    @Query("""
            SELECT ag FROM AdjustGrade ag
            JOIN FETCH ag.grade
            WHERE ag.adjust.id = :adjustId
            ORDER BY ag.grade.name
        """)
    List<AdjustGrade> findByAdjustId(@Param("adjustId") Long adjustId);

    @Query("""
            SELECT a FROM AdjustGrade a
            WHERE a.adjust.id = :adjustId
              AND a.grade.id = :gradeId
        """)
    Optional<AdjustGrade> findByAdjustIdAndGradeId(@Param("adjustId") Long adjustId, @Param("gradeId") Long gradeId);

    List<AdjustGrade> findByAdjustIdAndIsActiveTrue(Long adjustId);

    List<AdjustGrade> findByAdjustIdAndIsActive(Long adjustId, boolean isActive);
}
