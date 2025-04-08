package com.bongsco.api.adjust.annual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.entity.SalaryIncrementByRank;
import com.bongsco.api.adjust.common.entity.AdjustGrade;

@Repository
public interface SalaryIncrementByRankRepository extends JpaRepository<SalaryIncrementByRank, Long> {
    Optional<SalaryIncrementByRank> findByRankIdAndAdjustGradeId(Long rankId, Long adjustGradeId);

    List<SalaryIncrementByRank> findByAdjustGradeIn(List<AdjustGrade> adjustGrades);
}
