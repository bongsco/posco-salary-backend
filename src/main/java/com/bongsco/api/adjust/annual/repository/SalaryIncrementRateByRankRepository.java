package com.bongsco.api.adjust.annual.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.entity.SalaryIncrementRateByRank;

@Repository
public interface SalaryIncrementRateByRankRepository extends JpaRepository<SalaryIncrementRateByRank, Long> {
    Optional<SalaryIncrementRateByRank> findByRankIdAndAdjustIdAndGradeId(Long rankId, Long adjustId, Long gradeId);
}
