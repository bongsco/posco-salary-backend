package com.bongsco.poscosalarybackend.adjust.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.poscosalarybackend.adjust.domain.RankIncrementRate;

@Repository
public interface RankIncrementRateRepository extends JpaRepository<RankIncrementRate, Long> {
    RankIncrementRate findByRankIdAndAdjInfoIdAndGradeId(Long rankId, Long adjInfoId, Long gradeId);
}
