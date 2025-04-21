package com.bongsco.mobile.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.mobile.dto.response.AdjustInfoResponse;
import com.bongsco.mobile.dto.response.AdjustListResponse;
import com.bongsco.mobile.dto.response.ChartResponse;
import com.bongsco.mobile.entity.AdjustSubject;
import com.bongsco.mobile.repository.reflection.AdjustDetailProjection;
import com.bongsco.mobile.repository.reflection.ChartProjection;

@Repository
public interface AdjustSubjectRepository extends JpaRepository<AdjustSubject, Long> {

    @Query("""
        SELECT 
            a.year AS year, a.orderNumber AS orderNumber, 
            asj.finalStdSalary AS stdSalary, asj.hpoBonus AS hpoBonus, 
            ROUND((COALESCE(asj.finalStdSalary, 0.0) + COALESCE(asj.grade.baseSalary, 0.0)) / 24.0 + 12.0, 0) AS bonusPrice,
            asj.isInHpo AS isInHpo, 
            s.salaryIncrementRate AS  salaryIncrementRate, s.bonusMultiplier AS bonusMultiplier, 
            a.hpoSalaryIncrementByRank AS hpoSalaryIncrementByRank, a.hpoBonusMultiplier AS hpoBonusMultiplier
        FROM AdjustSubject asj
        JOIN Adjust a ON asj.adjust.id = a.id
        JOIN AdjustGrade ag ON asj.grade.id = ag.grade.id AND a.id = ag.adjust.id
        JOIN SalaryIncrementByRank s ON ag.id = s.adjustGrade.id AND asj.rank.id = s.rank.id
        WHERE asj.employee.id = :employeeId 
            AND a.isSubmitted = true
        ORDER BY asj.id DESC
        LIMIT 5
    """)
    List<ChartProjection> findFiveRecentChartData(@Param("employeeId") Long employeeId);

    @Query("""
        SELECT new com.bongsco.mobile.dto.response.AdjustInfoResponse(
            a.id, a.year, a.orderNumber, a.adjustType
        )
        FROM AdjustSubject asj
        JOIN Adjust a ON asj.adjust.id = a.id
        WHERE asj.employee.id = :employeeId
            AND a.isSubmitted = true
        ORDER BY asj.id DESC
    """)
    Page<AdjustInfoResponse> findAdjustInfo(@Param("employeeId") Long employeeId , Pageable pageable);

    @Query("""
        SELECT asj
        FROM AdjustSubject asj
        WHERE asj.adjust.id < :adjustId
            AND asj.employee.id = :employeeId
            AND asj.deleted != true
            AND asj.isSubject = true
        ORDER BY asj.adjust.id DESC
        LIMIT 1
        """
    )
    AdjustSubject findBeforeAdjSubject(@Param("adjustId") Long adjustId, @Param("employeeId") Long employeeId);


    @Query("""
        SELECT a.year AS year, a.orderNumber AS orderNumber, a.adjustType AS adjustType,
            a.author AS author, a.baseDate AS baseDate,a.exceptionStartDate as exceptionStartDate, a.exceptionEndDate as exceptionEndDate,
            e.name AS name, asj.grade.name AS gradeName, e.department.name AS departmentName, e.positionName AS positionName,
            e.hireDate AS hireDate, e.employmentType.name AS employmentTypeName, e.rank.name AS rankName,
            asj.isInHpo AS isInHpo, s.salaryIncrementRate AS salaryIncrementRate, s.bonusMultiplier AS bonusMultiplier,
            a.hpoSalaryIncrementByRank as hpoSalaryIncrementByRank, a.hpoBonusMultiplier AS hpoBonusMultiplier,
            asj.finalStdSalary as stdSalary, asj.hpoBonus AS hpoBonus, asj.isPaybandApplied as isPaybandApplied, e.stdSalary as beforeStdSalary
        FROM AdjustSubject asj
        JOIN Adjust a ON asj.adjust.id = a.id
        JOIN AdjustGrade ag ON asj.grade.id = ag.grade.id AND a.id = ag.adjust.id
        JOIN SalaryIncrementByRank s ON ag.id = s.adjustGrade.id AND asj.rank.id = s.rank.id
        JOIN Employee e ON asj.employee.id = e.id
        WHERE e.id = :employeeId
            AND a.id = :adjustId
            AND a.deleted = false
    """)
    AdjustDetailProjection findAdjustDetailProjection(@Param("adjustId") Long adjustId, @Param("employeeId") Long employeeId);
}
