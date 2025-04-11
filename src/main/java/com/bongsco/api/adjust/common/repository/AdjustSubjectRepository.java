package com.bongsco.api.adjust.common.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.dto.MainResultExcelDto;
import com.bongsco.api.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.HpoEmployee;
import com.bongsco.api.adjust.annual.repository.reflection.MainResultProjection;
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

    @Query(
        value = """
             SELECT 
                 e.empNum as empNum, e.name as name, g.name as gradeName, e.positionName as positionName, d.name as depName, r.code as rankCode,
                 e.stdSalaryIncrementRate as stdSalaryIncrementRate, asj.finalStdSalary as finalStdSalary, asj.stdSalary as stdSalary,
                 asj.hpoBonus as hpoBonus, asj.isInHpo as isInHpo, e.id as empId, asj.id as adjustSubjectId, g.id as gradeId, r.id as rankId, 
                 ag.id as adjustGradeId, s.bonusMultiplier as bonusMultiplier, s.salaryIncrementRate as salaryIncrementRate, 
                 asj.isPaybandApplied as isPaybandApplied, COALESCE(asj.finalStdSalary, 0) + COALESCE(asj.hpoBonus, 0) AS totalSalary
            FROM AdjustSubject asj
                JOIN Employee e ON e.id = asj.employee.id
                JOIN Grade g ON g.id = asj.grade.id
                JOIN Department d ON d.id = e.department.id
                JOIN Rank r ON r.id = asj.rank.id
                JOIN AdjustGrade ag ON ag.adjust.id = asj.adjust.id AND ag.grade.id = g.id
                JOIN SalaryIncrementByRank s ON s.adjustGrade.id = ag.id AND s.rank.id = r.id
            WHERE asj.adjust.id = :adjustId
                AND asj.isSubject = true
                AND e.empNum LIKE COALESCE(:filterEmpNum, e.empNum)
                AND e.name LIKE COALESCE(:filterName, e.name)
                AND g.name = COALESCE(:filterGrade, g.name)
                AND d.name LIKE COALESCE(:filterDepartment, d.name)
                AND r.code = COALESCE(:filterRank, r.code)
            """,
        countQuery = """
            SELECT COUNT(asj.id)
            FROM AdjustSubject asj
                JOIN Employee e ON e.id = asj.employee.id
                JOIN Grade g ON g.id = asj.grade.id
                JOIN Department d ON d.id = e.department.id
                JOIN Rank r ON r.id = asj.rank.id
                JOIN AdjustGrade ag ON ag.adjust.id = asj.adjust.id AND ag.grade.id = g.id
                JOIN SalaryIncrementByRank s ON s.adjustGrade.id = ag.id AND s.rank.id = r.id
            WHERE asj.adjust.id = :adjustId
                AND asj.isSubject = true
                AND e.empNum LIKE COALESCE(:filterEmpNum, e.empNum)
                AND e.name LIKE COALESCE(:filterName, e.name)
                AND g.name = COALESCE(:filterGrade, g.name)
                AND d.name LIKE COALESCE(:filterDepartment, d.name)
                AND r.code = COALESCE(:filterRank, r.code)
            """
    )
    Page<MainResultProjection> findResultDtoWithPagination(
        @Param("adjustId") Long adjustId,
        @Param("filterEmpNum") String filterEmpNum,
        @Param("filterName") String filterName,
        @Param("filterGrade") String filterGrade,
        @Param("filterDepartment") String filterDepartment,
        @Param("filterRank") String filterRank,
        Pageable pageable);

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
            SELECT asj.employee, asj.finalStdSalary
            FROM AdjustSubject asj
            WHERE asj.adjust.id = :adjustId
                AND asj.isSubject = true
        """)
    List<Object[]> findAdjustSubjectIncrementDtoByAdjustId(@Param("adjustId") Long adjustId);

    List<AdjustSubject> findAllByAdjustIdAndEmployeeIdIn(Long adjustId, List<Long> employeeIds);

    @Query("""
            SELECT new com.bongsco.api.adjust.annual.dto.MainResultExcelDto(
                e.empNum, e.name, g.name, e.positionName, d.name, r.code,
                e.stdSalaryIncrementRate, asj.finalStdSalary, asj.stdSalary,
                asj.hpoBonus, asj.isInHpo, e.id, asj.id, g.id, r.id, ag.id,
                s.bonusMultiplier, s.salaryIncrementRate, asj.isPaybandApplied
            )
            FROM AdjustSubject asj
            JOIN Employee e ON e.id = asj.employee.id
            JOIN Grade g ON g.id = asj.grade.id
            JOIN Department d ON d.id = e.department.id
            JOIN Rank r ON r.id = asj.rank.id
            JOIN AdjustGrade ag ON ag.adjust.id = asj.adjust.id AND ag.grade.id = g.id
            JOIN SalaryIncrementByRank s ON s.adjustGrade.id = ag.id AND s.rank.id = r.id
            WHERE asj.adjust.id = :adjustId
            AND asj.isSubject = true
        """)
    List<MainResultExcelDto> findAllResultDtoByAdjustId(@Param("adjustId") Long adjustId);
}
