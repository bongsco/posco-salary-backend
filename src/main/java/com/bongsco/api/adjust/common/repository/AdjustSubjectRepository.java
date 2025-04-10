package com.bongsco.api.adjust.common.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.dto.AdjustSubjectIncrementDto;
import com.bongsco.api.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.HpoEmployee;
import com.bongsco.api.adjust.common.dto.AdjustSubjectSalaryDto;
import com.bongsco.api.adjust.common.entity.AdjustSubject;

@Repository
public interface AdjustSubjectRepository extends JpaRepository<AdjustSubject, Long> {

    List<AdjustSubject> findByAdjust_Id(Long adjustId);

    @Query("""
        SELECT new com.bongsco.api.adjust.annual.dto.response.HpoEmployee(
            emp.id,
            emp.empNum,
            emp.name, 
            dept.name,
            g.name,
            r.code,
            asj.isInHpo
        )
        FROM AdjustSubject asj          
        JOIN asj.employee emp
        JOIN emp.department dept
        JOIN emp.grade g
        JOIN emp.rank r    
        WHERE asj.adjust.id = :adjustId 
        AND asj.isSubject = true
        """)
    List<HpoEmployee> findByAdjustIdAndIsSubjectTrue(Long adjustId);

    @Query("""
        SELECT asj
        FROM AdjustSubject asj
        WHERE asj.adjust.id = :adjustId
            AND (
                asj.employee.empNum LIKE %:searchKey%
                OR asj.employee.name LIKE %:searchKey%
            )
        """
    )
    List<AdjustSubject> findByAdjustIdAndSearchKey(Long adjustId, String searchKey);

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
    AdjustSubject findBeforeAdjSubject(Long adjustId, Long employeeId);

    Optional<AdjustSubject> findByAdjustIdAndEmployeeId(Long adjustId, Long employeeId);

    @Query("""
        SELECT new com.bongsco.api.adjust.common.dto.AdjustSubjectSalaryDto(
            asj.id,
            e.id,
            asj.stdSalary,
            asj.finalStdSalary,
            asj.isPaybandApplied,
            g.id,
            e.empNum,
            e.name,
            d.name,
            g.name,
            e.positionName,
            r.code,
            pc.upperBound,
            g.baseSalary
        )
        FROM AdjustSubject asj
        JOIN asj.employee e
        JOIN e.department d
        JOIN e.rank r
        JOIN asj.grade g
        JOIN PaybandCriteria pc ON pc.grade.id = g.id
        WHERE asj.adjust.id = :adjustId
          AND pc.adjust.id = :adjustId
          AND asj.deleted != true
          AND pc.deleted != true
          AND asj.stdSalary > g.baseSalary * (pc.upperBound / 100.0)
        """)
    List<AdjustSubjectSalaryDto> findUpperExceededSubjects(@Param("adjustId") Long adjustId);

    @Query("""
        SELECT new com.bongsco.api.adjust.common.dto.AdjustSubjectSalaryDto(
            asj.id,
            e.id,
            asj.stdSalary,
            asj.finalStdSalary,
            asj.isPaybandApplied,
            g.id,
            e.empNum,
            e.name,
            d.name,
            g.name,
            e.positionName,
            r.code,
            pc.lowerBound,
            g.baseSalary
        )
        FROM AdjustSubject asj
        JOIN asj.employee e
        JOIN e.department d
        JOIN e.rank r
        JOIN asj.grade g
        JOIN PaybandCriteria pc ON pc.grade.id = g.id
        WHERE asj.adjust.id = :adjustId
          AND pc.adjust.id = :adjustId
          AND asj.deleted != true
          AND pc.deleted != true
          AND asj.stdSalary < g.baseSalary * (pc.lowerBound / 100.0)
        """)
    List<AdjustSubjectSalaryDto> findLowerExceededSubjects(@Param("adjustId") Long adjustId);

    @Query("""
            SELECT new com.bongsco.api.adjust.annual.dto.response.EmployeeResponse(
                e.id,
                e.empNum,
                e.name,
                e.hireDate,
                r.code,
                s.isSubject
            )
            FROM AdjustSubject s
            JOIN s.employee e
            JOIN e.rank r
            WHERE s.adjust.id = :adjustId
        """)
    List<EmployeeResponse> findAllEmployeeResponsesByAdjustInfoId(@Param("adjustId") Long adjustId);

    @Query("SELECT s.employee.id FROM AdjustSubject s WHERE s.adjust.id = :adjustId")
    Set<Long> findEmployeeIdsByAdjustId(@Param("adjustId") Long adjustId);

    @Query("""
                  SELECT asj, new com.bongsco.api.adjust.annual.dto.AdjustSubjectSalaryCalculateDto(
                  asj.employee.id,
                  asj.grade.baseSalary,
                  s.salaryIncrementRate,
                  s.bonusMultiplier
                  )
                  FROM AdjustSubject asj
                  JOIN AdjustGrade ag ON ag.grade.id = asj.grade.id AND asj.adjust.id = ag.adjust.id
                  JOIN SalaryIncrementByRank s ON asj.rank.id = s.rank.id AND ag.id = s.adjustGrade.id
                  WHERE asj.adjust.id = :adjustId
                  AND asj.isSubject = true
        """)
    List<Object[]> findDtoByAdjustId(@Param("adjustId") Long adjustId);

    @Query("""
            SELECT new com.bongsco.api.adjust.annual.dto.AdjustSubjectIncrementDto(
                asj.employee.id,
                asj.finalStdSalary
            )
            FROM AdjustSubject asj
            WHERE asj.adjust.id = :adjustId
                AND asj.isSubject = true
        """)
    List<AdjustSubjectIncrementDto> findAdjustSubjectIncrementDtoByAdjustId(@Param("adjustId") Long adjustId);

    List<AdjustSubject> findAllByAdjustIdAndEmployeeIdIn(Long adjustId, List<Long> employeeIds);
}
