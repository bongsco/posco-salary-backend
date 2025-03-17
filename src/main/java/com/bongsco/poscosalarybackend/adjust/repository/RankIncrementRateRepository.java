package com.bongsco.poscosalarybackend.adjust.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.poscosalarybackend.adjust.domain.RankIncrementRate;

@Repository
public interface RankIncrementRateRepository extends JpaRepository<RankIncrementRate, Long> {
    Optional<RankIncrementRate> findByRankIdAndAdjInfoIdAndGradeId(Long rankId, Long adjInfoId, Long gradeId);
}
