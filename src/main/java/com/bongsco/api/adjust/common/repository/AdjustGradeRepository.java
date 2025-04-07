package com.bongsco.api.adjust.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.adjust.common.entity.AdjustGrade;
public interface AdjustGradeRepository extends JpaRepository<AdjustGrade, Long> {
    List<AdjustGrade> findByAdjustId(Long adjustId);
}
