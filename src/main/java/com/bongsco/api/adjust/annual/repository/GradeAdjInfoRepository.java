package com.bongsco.api.adjust.annual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.adjust.annual.domain.GradeAdjInfo;
public interface GradeAdjInfoRepository extends JpaRepository<GradeAdjInfo, Long> {

    void deleteByAdjInfoId(Long adjInfoId);

    List<GradeAdjInfo> findByAdjInfoId(Long adjInfoId);
}
