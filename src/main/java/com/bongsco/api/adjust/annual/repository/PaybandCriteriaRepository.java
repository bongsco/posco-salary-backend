package com.bongsco.api.adjust.annual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.domain.PaybandCriteria;

@Repository
public interface PaybandCriteriaRepository extends JpaRepository<PaybandCriteria, Long> {
    public List<PaybandCriteria> findByAdjInfo_Id(Long id);

    public Optional<PaybandCriteria> findByAdjInfo_IdAndGrade_Id(Long adjInfoId, Long gradeId);

    void deleteByIdIn(List<Long> paybandIds);
}
