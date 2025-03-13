package com.bongsco.poscosalarybackend.adjust.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;
import com.bongsco.poscosalarybackend.adjust.domain.GradeAdjInfo;
public interface GradeAdjInfoRepository extends JpaRepository<GradeAdjInfo, Long> {
    void deleteAllByAdjInfo(AdjInfo adjInfo);
}
