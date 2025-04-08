package com.bongsco.api.adjust.annual.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.entity.SalaryIncrementByRank;

@Repository
public interface SalaryIncrementRateByRankRepository extends JpaRepository<SalaryIncrementByRank, Long> {
    Optional<SalaryIncrementByRank> findByRankIdAndAdjustGradeId(Long rankId, Long adjustGradeId);
}
