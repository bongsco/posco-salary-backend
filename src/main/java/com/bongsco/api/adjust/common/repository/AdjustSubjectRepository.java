package com.bongsco.api.adjust.common.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.dto.HpoPerDepartmentDto;
import com.bongsco.api.adjust.annual.dto.MainResultExcelDto;
import com.bongsco.api.adjust.annual.dto.SalaryPerGradeDto;
import com.bongsco.api.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.HpoEmployee;
import com.bongsco.api.adjust.annual.repository.reflection.MainResultProjection;
import com.bongsco.api.adjust.common.dto.AdjustSubjectSalaryDto;
import com.bongsco.api.adjust.common.entity.AdjustSubject;

@Repository
public interface AdjustSubjectRepository extends JpaRepository<AdjustSubject, Long> {
    @Modifying
    @Query(value = """
        UPDATE AdjustSubject adjs
            SET adjs.isPaybandApplied = "NONE"
        WHERE adjs.adjust.id = :adjustId
        """)
    void updatePaybandAppliedTypeByAdjustId(@Param("adjustId") Long adjustId);

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
                e.emp_num AS empNum, e.name AS name, g.name AS gradeName, e.position_name AS positionName, d.name AS depName, r.code AS rankCode,
                e.std_salary_increment_rate AS stdSalaryIncrementRate, asj.final_std_salary AS finalStdSalary, asj.std_salary AS stdSalary,
                asj.hpo_bonus AS hpoBonus, asj.is_in_hpo AS isInHpo, e.id AS empId, asj.id AS adjustSubjectId, g.id AS gradeId, r.id AS rankId,
                ag.id AS adjustGradeId, s.bonus_multiplier AS bonusMultiplier, s.salary_increment_rate AS salaryIncrementRate,
                asj.is_payband_applied AS isPaybandApplied,
                COALESCE(asj.final_std_salary, 0) + COALESCE(asj.hpo_bonus, 0) AS totalSalary
            FROM adjust_subject asj
            JOIN employee e ON e.id = asj.employee_id
            JOIN grade g ON g.id = asj.grade_id
            JOIN department d ON d.id = e.dept_id
            JOIN rank r ON r.id = asj.rank_id
            JOIN adjust_grade ag ON ag.adjust_id = asj.adjust_id AND ag.grade_id = g.id
            JOIN salary_increment_by_rank s ON s.adjust_grade_id = ag.id AND s.rank_id = r.id
            WHERE asj.adjust_id = :adjustId
                AND asj.is_subject = true
                AND (COALESCE(array_length(CAST(:filterEmpNum AS text[]), 1), 0) = 0 OR e.emp_num LIKE ANY(CAST(:filterEmpNum AS text[])))
                AND (COALESCE(array_length(CAST(:filterName AS text[]), 1), 0) = 0 OR e.name LIKE ANY(CAST(:filterName AS text[])))
                AND (COALESCE(array_length(CAST(:filterGrade AS text[]), 1), 0) = 0 OR g.name = ANY (CAST(:filterGrade AS text[])))
                AND (COALESCE(array_length(CAST(:filterDepartment AS text[]), 1), 0) = 0 OR d.name LIKE ANY(CAST(:filterDepartment AS text[])))
                AND (COALESCE(array_length(CAST(:filterRank AS text[]), 1), 0) = 0 OR r.code = ANY (CAST(:filterRank AS text[])))
            """,
        countQuery = """
            SELECT COUNT(asj.id)
            FROM adjust_subject asj
            JOIN employee e ON e.id = asj.employee_id
            JOIN grade g ON g.id = asj.grade_id
            JOIN department d ON d.id = e.dept_id
            JOIN rank r ON r.id = asj.rank_id
            JOIN adjust_grade ag ON ag.adjust_id = asj.adjust_id AND ag.grade_id = g.id
            JOIN salary_increment_by_rank s ON s.adjust_grade_id = ag.id AND s.rank_id = r.id
            WHERE asj.adjust_id = :adjustId
                AND asj.is_subject = true
                AND (COALESCE(array_length(CAST(:filterEmpNum AS text[]), 1), 0) = 0 OR e.emp_num LIKE ANY(CAST(:filterEmpNum AS text[])))
                AND (COALESCE(array_length(CAST(:filterName AS text[]), 1), 0) = 0 OR e.name LIKE ANY(CAST(:filterName AS text[])))
                AND (COALESCE(array_length(CAST(:filterGrade AS text[]), 1), 0) = 0 OR g.name = ANY (CAST(:filterGrade AS text[])))
                AND (COALESCE(array_length(CAST(:filterDepartment AS text[]), 1), 0) = 0 OR d.name LIKE ANY(CAST(:filterDepartment AS text[])))
                AND (COALESCE(array_length(CAST(:filterRank AS text[]), 1), 0) = 0 OR r.code = ANY (CAST(:filterRank AS text[])))
            """,
        nativeQuery = true
    )
    Page<MainResultProjection> findResultDtoWithPagination(
        @Param("adjustId") Long adjustId,
        @Param("filterEmpNum") String[] filterEmpNum,
        @Param("filterName") String[] filterName,
        @Param("filterGrade") String[] filterGrade,
        @Param("filterDepartment") String[] filterDepartment,
        @Param("filterRank") String[] filterRank,
        Pageable pageable
    );

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

    @Modifying
    @Query("UPDATE AdjustSubject s SET s.deleted = true WHERE s.adjust.id = :adjustId AND s.employee.id IN :employeeIds AND s.deleted = false")
    void softDeleteByAdjustIdAndEmployeeIdIn(@Param("adjustId") Long adjustId,
        @Param("employeeIds") Set<Long> employeeIds);

    @Query("""
        SELECT new com.bongsco.api.adjust.annual.dto.SalaryPerGradeDto(
            g.name, SUM (asj.finalStdSalary), SUM (asj.hpoBonus)
            )
        FROM AdjustSubject asj
        JOIN Grade g ON asj.grade.id = g.id
        WHERE asj.adjust.id = :adjustId
        AND asj.isSubject = true
        AND asj.deleted=false
        GROUP BY g.name
        ORDER BY g.name
    """
    )
    List<SalaryPerGradeDto> findSalaryPerDto(@Param("adjustId") Long adjustId);

    @Query("""
        SELECT new com.bongsco.api.adjust.annual.dto.HpoPerDepartmentDto(
            d.name, COUNT(e)
        )
        FROM  AdjustSubject asj
        JOIN Employee e ON e.id = asj.employee.id
        JOIN Department d ON e.department.id = d.id
        WHERE asj.adjust.id = :adjustId
        AND asj.isSubject = true
        AND asj.deleted=false
        AND asj.isInHpo = true
        GROUP BY d.name
        ORDER BY COUNT(e) DESC
        LIMIT 10
""")
    List<HpoPerDepartmentDto> findHpoPerDepartmentDto(@Param("adjustId") Long adjustId);

}
