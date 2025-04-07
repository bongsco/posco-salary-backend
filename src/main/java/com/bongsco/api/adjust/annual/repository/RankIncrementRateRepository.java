package com.bongsco.api.adjust.annual.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.domain.RankIncrementRate;

@Repository
public interface RankIncrementRateRepository extends JpaRepository<RankIncrementRate, Long> {
    Optional<RankIncrementRate> findByRankIdAndAdjInfoIdAndGradeId(Long rankId, Long adjInfoId, Long gradeId);
}
