package com.bongsco.poscosalarybackend.adjust.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.poscosalarybackend.adjust.domain.GradeAdjInfo;
public interface GradeAdjInfoRepository extends JpaRepository<GradeAdjInfo, Long> {

    void deleteByAdjInfoId(Long adjInfoId);

    List<GradeAdjInfo> findByAdjInfoId(Long adjInfoId);
}
