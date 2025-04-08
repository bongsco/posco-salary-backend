package com.bongsco.api.adjust.annual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.entity.PaybandCriteria;

@Repository
public interface PaybandCriteriaRepository extends JpaRepository<PaybandCriteria, Long> {
    List<PaybandCriteria> findByAdjustId(Long id);

    Optional<PaybandCriteria> findByAdjustIdAndGradeId(Long adjustId, Long gradeId);
}
