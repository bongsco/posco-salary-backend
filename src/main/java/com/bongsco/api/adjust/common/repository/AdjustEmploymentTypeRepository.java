package com.bongsco.api.adjust.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bongsco.api.adjust.common.entity.AdjustEmploymentType;
public interface AdjustEmploymentTypeRepository extends JpaRepository<AdjustEmploymentType, Long> {
    List<AdjustEmploymentType> findByAdjustId(Long adjInfoId);
}
