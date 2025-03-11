package com.bongsco.poscosalarybackend.adjust.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.poscosalarybackend.adjust.domain.AdjSubject;

@Repository
public interface EmployeeRepository extends JpaRepository<AdjSubject, Long> {
    List<AdjSubject> findByAdjInfo_Id(long id);

    @Query("SELECT asj FROM AdjSubject asj JOIN asj.employee e WHERE asj.adjInfo.id = :adjInfoId AND (e.empNum = :searchKey or e.name = :searchKey)")
    List<AdjSubject> findByAdjInfoIdAndEmployeeName(@Param("adjInfoId") long adjInfoId,
        @Param("searchKey") String searchKey);
}
