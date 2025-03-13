package com.bongsco.poscosalarybackend.adjust.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;

@Repository
public interface AdjustRepository extends JpaRepository<AdjInfo, Long> {

    // startYear 와 endYear 둘 다 있는 경우
    List<AdjInfo> findByYearBetween(Long startYear, Long endYear);

    @Query("SELECT adj FROM AdjInfo adj WHERE adj.adjType='ANNUAL_SALARY_ADJUSTMENT' AND adj.id< :id ORDER BY adj.id DESC ")
    List<AdjInfo> findLatestAdjustInfo(@Param("id") Long id);
}
