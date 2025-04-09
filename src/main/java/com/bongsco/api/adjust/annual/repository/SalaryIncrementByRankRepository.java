package com.bongsco.api.adjust.annual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.entity.SalaryIncrementByRank;

@Repository
public interface SalaryIncrementByRankRepository extends JpaRepository<SalaryIncrementByRank, Long> {
    Optional<SalaryIncrementByRank> findByRankIdAndAdjustGradeId(Long rankId, Long adjustGradeId);

    @Query("""
            SELECT s FROM SalaryIncrementByRank s
            WHERE s.adjustGrade.adjust.id = :adjustId
              AND s.adjustGrade.grade.name IN :gradeNames
        """)
    List<SalaryIncrementByRank> findByAdjustIdAndGradeNames(
        @Param("adjustId") Long adjustId,
        @Param("gradeNames") List<String> gradeNames
    );
}
