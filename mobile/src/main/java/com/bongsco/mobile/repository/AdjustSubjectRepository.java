package com.bongsco.mobile.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.mobile.dto.response.ChartResponse;
import com.bongsco.mobile.entity.AdjustSubject;
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
}
