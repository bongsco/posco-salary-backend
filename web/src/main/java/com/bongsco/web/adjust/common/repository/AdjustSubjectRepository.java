package com.bongsco.web.adjust.common.repository;

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

import com.bongsco.web.adjust.annual.dto.HpoPerDepartmentDto;
import com.bongsco.web.adjust.annual.dto.MainResultExcelDto;
import com.bongsco.web.adjust.annual.dto.SalaryPerGradeDto;
import com.bongsco.web.adjust.annual.dto.UncalculatedDto;
import com.bongsco.web.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.web.adjust.annual.dto.response.HpoEmployee;
import com.bongsco.web.adjust.annual.repository.reflection.MainResultProjection;
import com.bongsco.web.adjust.common.dto.AdjustSubjectSalaryDto;
import com.bongsco.web.adjust.common.entity.AdjustSubject;
import com.bongsco.web.adjust.common.repository.reflection.EmployeeAndSalaryProjection;

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
        SELECT new com.bongsco.web.adjust.annual.dto.response.HpoEmployee(
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

    Optional<AdjustSubject> findByAdjustIdAndEmployeeId(Long adjustId, Long employeeId);

    @Query("""
        SELECT new com.bongsco.web.adjust.common.dto.AdjustSubjectSalaryDto(
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
        JOIN PaybandCriteria pc ON pc.adjustGrade.grade.id = g.id
        WHERE asj.adjust.id = :adjustId
          AND pc.adjustGrade.adjust.id = :adjustId
          AND asj.deleted != true
          AND pc.deleted != true
          AND asj.stdSalary > g.baseSalary * (pc.upperBound / 100.0)
        """)
    List<AdjustSubjectSalaryDto> findUpperExceededSubjects(@Param("adjustId") Long adjustId);

    @Query("""
        SELECT new com.bongsco.web.adjust.common.dto.AdjustSubjectSalaryDto(
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
        JOIN PaybandCriteria pc ON pc.adjustGrade.grade.id = g.id
        WHERE asj.adjust.id = :adjustId
          AND pc.adjustGrade.adjust.id = :adjustId
          AND asj.deleted != true
          AND pc.deleted != true
          AND asj.stdSalary < g.baseSalary * (pc.lowerBound / 100.0)
        """)
    List<AdjustSubjectSalaryDto> findLowerExceededSubjects(@Param("adjustId") Long adjustId);

    @Query(
        value = """
            SELECT 
                e.emp_num AS empNum, e.name AS name, g.name AS gradeName, e.position_name AS positionName, d.name AS depName, r.code AS rankCode,
                asj.final_std_salary AS finalStdSalary, asj.std_salary AS stdSalary,
                asj.hpo_bonus AS hpoBonus, asj.is_in_hpo AS isInHpo, e.id AS empId, asj.id AS adjustSubjectId, g.id AS gradeId, r.id AS rankId,
                ag.id AS adjustGradeId, s.bonus_multiplier AS bonusMultiplier, s.salary_increment_rate AS salaryIncrementRate,
                asj.is_payband_applied AS isPaybandApplied,
                COALESCE(asj.final_std_salary, 0) + COALESCE(asj.hpo_bonus, 0) AS totalSalary, e.std_salary AS beforeStdSalary, e.hpo_bonus AS beforeHpoBonus,
                pc.upper_bound_memo as upperBoundMemo, pc.lower_bound_memo as lowerBoundMemo
            FROM adjust_subject asj
            JOIN employee e ON e.id = asj.employee_id
            JOIN grade g ON g.id = asj.grade_id
            JOIN department d ON d.id = e.dept_id
            JOIN rank r ON r.id = asj.rank_id
            JOIN adjust_grade ag ON ag.adjust_id = asj.adjust_id AND ag.grade_id = g.id
            JOIN salary_increment_by_rank s ON s.adjust_grade_id = ag.id AND s.rank_id = r.id
            JOIN payband_criteria pc ON ag.id = pc.adjust_grade_id
            WHERE asj.adjust_id = :adjustId
                AND asj.is_subject = true
                AND (asj.deleted IS NULL OR asj.deleted = false)
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
            JOIN payband_criteria pc ON ag.id = pc.adjust_grade_id
            WHERE asj.adjust_id = :adjustId
                AND asj.is_subject = true
                AND (asj.deleted IS NULL OR asj.deleted = false)
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
            SELECT new com.bongsco.web.adjust.annual.dto.response.EmployeeResponse(
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
                  SELECT asj, new com.bongsco.web.adjust.annual.dto.AdjustSubjectSalaryCalculateDto(
                  asj.employee.id,
                  asj.grade.baseSalary,
                  s.salaryIncrementRate,
                  s.bonusMultiplier,
                  asj.employee.stdSalary
                  )
                  FROM AdjustSubject asj
                  JOIN AdjustGrade ag ON ag.grade.id = asj.grade.id AND asj.adjust.id = ag.adjust.id
                  JOIN SalaryIncrementByRank s ON asj.rank.id = s.rank.id AND ag.id = s.adjustGrade.id
                  WHERE asj.adjust.id = :adjustId
                  AND asj.isSubject = true
                  AND (asj.deleted IS NULL OR asj.deleted = false)
        """)
    List<Object[]> findDtoByAdjustId(@Param("adjustId") Long adjustId);

    @Query("""
            SELECT asj.employee as employee, asj as adjustSubject, pc.upperBoundMemo as upperBoundMemo, pc.lowerBoundMemo as lowerBoundMemo
            FROM AdjustSubject asj
            JOIN AdjustGrade ag ON asj.adjust.id = ag.adjust.id AND asj.grade.id = ag.grade.id
            JOIN PaybandCriteria pc ON ag.id = pc.adjustGrade.id
            WHERE asj.adjust.id = :adjustId
                AND asj.isSubject = true
                AND (asj.deleted IS NULL OR asj.deleted = false)
        """)
    List<EmployeeAndSalaryProjection> findAdjustSubjectIncrementDtoByAdjustId(@Param("adjustId") Long adjustId);

    @Query("""
        SELECT asj
        FROM AdjustSubject asj
        WHERE asj.adjust.id < :adjustId
            AND asj.employee.id = :employeeId
            AND asj.deleted != true
            AND asj.isSubject = true
            AND asj.adjust.deleted != true
            AND asj.adjust.isSubmitted = true
        ORDER BY asj.adjust.id DESC
        LIMIT 1
        """
    )
    AdjustSubject findBeforeAdjSubject(Long adjustId, Long employeeId);

    List<AdjustSubject> findAllByAdjustIdAndEmployeeIdIn(Long adjustId, List<Long> employeeIds);

    @Query("""
            SELECT new com.bongsco.web.adjust.annual.dto.MainResultExcelDto(
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

    @Query(value = """
        SELECT 
            CAST(a.year AS VARCHAR) AS adjust_cycle,
            COUNT(DISTINCT s.employee_id) AS headcount,
            (
                SELECT COUNT(DISTINCT s2.employee_id)
                FROM adjust_subject s2
                JOIN adjust a2 ON a2.id = s2.adjust_id
                WHERE a2.adjust_type = 'ANNUAL'
                  AND a2.year = a.year - 1
                  AND s2.is_subject = TRUE
            ) AS prev_year_headcount
        FROM adjust_subject s
        JOIN adjust a ON s.adjust_id = a.id
        WHERE s.is_subject = TRUE
          AND a.adjust_type = 'ANNUAL'
          AND (s.deleted IS NULL OR s.deleted = false)
        GROUP BY a.year
        ORDER BY a.year
        """, nativeQuery = true)
    List<Object[]> getHeadcountTrendRaw();

    @Query(value = """
            SELECT 
                g.name AS grade,
                COUNT(*) AS count,
                ROUND(COUNT(*) * 100.0 / total.total_count, 1) AS percentage
            FROM adjust_subject s
            JOIN grade g ON s.grade_id = g.id
            JOIN adjust a ON s.adjust_id = a.id
            JOIN (
                SELECT COUNT(*) AS total_count
                FROM adjust_subject s2
                JOIN adjust a2 ON s2.adjust_id = a2.id
                WHERE a2.adjust_type = 'ANNUAL'
                  AND a2.id = :adjustId
                  AND s2.is_subject = TRUE
            ) total ON TRUE
            WHERE a.adjust_type = 'ANNUAL'
              AND a.id = :adjustId
              AND s.is_subject = TRUE
              AND (s.deleted IS NULL OR s.deleted = false)
            GROUP BY g.name, total.total_count
            ORDER BY g.name
        """, nativeQuery = true)
    List<Object[]> getGradeDistribution(@Param("adjustId") Long adjustId);

    @Query(value = """
            SELECT 
                et.name AS employment_type,
                COUNT(*) AS count,
                ROUND(COUNT(*) * 100.0 / total.total_count, 1) AS percentage
            FROM adjust_subject s
            JOIN adjust a ON a.id = s.adjust_id
            JOIN employee e ON e.id = s.employee_id
            JOIN employment_type et ON et.id = e.employment_type_id
            JOIN (
                SELECT COUNT(*) AS total_count
                FROM adjust_subject s2
                JOIN adjust a2 ON a2.id = s2.adjust_id
                WHERE a2.adjust_type = 'ANNUAL'
                  AND s2.is_subject = TRUE
                  AND a2.id = :adjustId
            ) total ON TRUE
            WHERE a.adjust_type = 'ANNUAL'
              AND a.id = :adjustId
              AND s.is_subject = TRUE
              AND (s.deleted IS NULL OR s.deleted = false)
            GROUP BY et.name, total.total_count
            ORDER BY et.name
        """, nativeQuery = true)
    List<Object[]> getEmploymentTypeDistribution(@Param("adjustId") Long adjustId);

    @Query(value = """
            SELECT 
                EXTRACT(YEAR FROM age(current_date, e.hire_date))::int AS year,
                COUNT(*) AS count
            FROM adjust_subject s
            JOIN adjust a ON a.id = s.adjust_id
            JOIN employee e ON e.id = s.employee_id
            WHERE a.adjust_type = 'ANNUAL'
              AND a.id = :adjustId
              AND s.is_subject = TRUE
              AND (s.deleted IS NULL OR s.deleted = false)
              AND e.hire_date IS NOT NULL
            GROUP BY EXTRACT(YEAR FROM age(current_date, e.hire_date))::int
            ORDER BY year
        """, nativeQuery = true)
    List<Object[]> getTenureDistribution(@Param("adjustId") Long adjustId);

    @Query(value = """
            SELECT 
                SUM(COALESCE(s.final_std_salary, 0) + COALESCE(s.hpo_bonus, 0))
            FROM adjust_subject s
            JOIN adjust a ON a.id = s.adjust_id
            WHERE a.adjust_type = 'ANNUAL'
              AND a.id = :adjustId
              AND s.is_subject = TRUE
              AND (s.deleted IS NULL OR s.deleted = false)
        """, nativeQuery = true)
    Long getCurrentTotalFinalSalary(@Param("adjustId") Long adjustId);

    @Query(value = """
            SELECT 
                SUM(COALESCE(s.final_std_salary, 0) + COALESCE(s.hpo_bonus, 0))
            FROM adjust_subject s
            JOIN adjust a ON a.id = s.adjust_id
            WHERE a.adjust_type = 'ANNUAL'
              AND a.year = :prevYear
              AND s.is_subject = TRUE
              AND (s.deleted IS NULL OR s.deleted = false)
        """, nativeQuery = true)
    Long getPrevTotalFinalSalary(@Param("prevYear") int prevYear);

    @Query(value = """
            SELECT 
                CASE 
                    WHEN total_salary < 10000000 THEN '4천만원 미만'
                    WHEN total_salary BETWEEN 10000000 AND 19999999 THEN '1천만원 ~ 2천만원'
                    WHEN total_salary BETWEEN 20000000 AND 29999999 THEN '2천만원 ~ 3천만원'
                    WHEN total_salary BETWEEN 30000000 AND 39999999 THEN '3천만원 ~ 4천만원'
                    WHEN total_salary BETWEEN 40000000 AND 49999999 THEN '4천만원 ~ 5천만원'
                    WHEN total_salary BETWEEN 50000000 AND 59999999 THEN '5천만원 ~ 6천만원'
                    ELSE '9천만원 이상'
                END AS salary_range,
                COUNT(*) AS count
            FROM (
                SELECT COALESCE(final_std_salary, 0) + COALESCE(hpo_bonus, 0) AS total_salary
                FROM adjust_subject
                WHERE adjust_id = :adjustId
                  AND is_subject = TRUE
                  AND (deleted IS NULL OR deleted = false)
            ) AS sub
            GROUP BY salary_range
            ORDER BY salary_range
        """, nativeQuery = true)
    List<Object[]> getSalaryRangeDistribution(@Param("adjustId") Long adjustId);

    @Query(value = """
            SELECT 
                AVG(COALESCE(s.final_std_salary, 0) + COALESCE(s.hpo_bonus, 0))
            FROM adjust_subject s
            WHERE s.adjust_id = :adjustId
              AND s.is_subject = TRUE
              AND (s.deleted IS NULL OR s.deleted = false)
        """, nativeQuery = true)
    Double getAverageFinalSalary(@Param("adjustId") Long adjustId);

    @Query(value = """
            SELECT 
                a.year AS adjust_year,
                g.name AS grade,
                SUM(COALESCE(s.final_std_salary, 0) + COALESCE(s.hpo_bonus, 0)) AS total_salary
            FROM adjust_subject s
            JOIN adjust a ON s.adjust_id = a.id
            JOIN grade g ON s.grade_id = g.id
            WHERE a.adjust_type = 'ANNUAL'
              AND s.is_subject = TRUE
              AND (s.deleted IS NULL OR s.deleted = false)
            GROUP BY a.year, g.name
            ORDER BY a.year, g.name
        """, nativeQuery = true)
    List<Object[]> getSalaryTrendByGrade();

    @Query("""
            SELECT new com.bongsco.web.adjust.annual.dto.SalaryPerGradeDto(
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
            SELECT new com.bongsco.web.adjust.annual.dto.UncalculatedDto(
                asj.grade.name, asj.stdSalary, asj.hpoBonus, asj.isPaybandApplied, pc.upperBoundMemo, pc.lowerBoundMemo
                )
            FROM AdjustSubject asj
            JOIN AdjustGrade ag ON asj.grade.id = ag.grade.id AND ag.adjust.id = :adjustId
            JOIN PaybandCriteria pc ON ag.id = pc.adjustGrade.id
            WHERE asj.adjust.id = :adjustId
            AND asj.isSubject = true
            AND asj.deleted=false
        """
    )
    List<UncalculatedDto> findUncalculatedDto(@Param("adjustId") Long adjustId);

    @Query("""
                SELECT new com.bongsco.web.adjust.annual.dto.HpoPerDepartmentDto(
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
