package com.bongsco.api.adjust.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.dto.MainResultDto;
import com.bongsco.api.adjust.common.dto.AdjSubjectSalaryDto;
import com.bongsco.api.adjust.common.entity.AdjustSubject;

@Repository
public interface AdjustSubjectRepository extends JpaRepository<AdjustSubject, Long> {
    @Query("""
        SELECT asj
        FROM AdjustSubject asj
        WHERE asj.adjust.id = :adjustId
        """
    )
    List<AdjustSubject> findByAdjustId(Long adjustId);

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
        ORDER BY asj.adjust.id DESC
        LIMIT 1
        """
    )
    AdjustSubject findBeforeAdjSubject(Long adjustId, Long employeeId);

    @Query("""
        SELECT asj
        FROM AdjustSubject asj
        JOIN asj.employee e
        WHERE asj.adjust.id = :adjustId
            AND (e.empNum LIKE %:searchKey% OR e.name LIKE %:searchKey%)
            AND asj.deleted != true
            AND e.deleted != true
        """
    )
    List<AdjustSubject> findByAdjustIdAndEmployeeName(
        @Param("adjustId") Long adjustId,
        @Param("searchKey") String searchKey
    );

    Optional<AdjustSubject> findByAdjustIdAndEmployeeId(Long adjustId, Long employeeId);

    @Query("""
        SELECT new com.bongsco.api.adjust.common.dto.AdjSubjectSalaryDto(
            asj.id,
            asj.employee.id,
            asj.stdSalary,
            asj.finalStdSalary,
            asj.isPaybandApplied,
            asj.grade.id,
            asj.employee.empNum,
            asj.employee.name,
            asj.employee.department.name,
            pc.grade.name,
            asj.employee.positionName,
            asj.employee.rank.name,
            pc.upperBound
        )
        FROM AdjustSubject asj
        JOIN PaybandCriteria pc ON pc.grade.id = asj.grade.id 
        WHERE asj.adjust.id = :adjustId
            AND pc.adjust.id = :adjustId
            AND asj.isSubject = true
            AND asj.deleted != true AND pc.deleted != true AND asj.isSubject = true
        """
    )
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndUpper(@Param("adjustId") Long adjustId);

    @Query("""
        SELECT new com.bongsco.api.adjust.common.dto.AdjSubjectSalaryDto(
            asj.id,
            asj.employee.id,
            asj.stdSalary,
            asj.finalStdSalary,
            asj.isPaybandApplied,
            asj.grade.id,
            asj.employee.empNum,
            asj.employee.name,
            asj.employee.department.name,
            pc.grade.name,
            asj.employee.positionName,
            asj.employee.rank.name,
            pc.lowerBound
        )
        FROM AdjustSubject asj
        JOIN PaybandCriteria pc ON pc.grade.id = asj.grade.id
        WHERE asj.adjust.id = :adjustId
            AND pc.adjust.id = :adjustId
            AND asj.isSubject = true
            AND asj.deleted!=true
            AND pc.deleted!=true
            AND asj.isSubject=true
        """
    )
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndLower(@Param("adjustId") Long adjustId);

    @Query("""
        SELECT new com.bongsco.api.adjust.common.dto.AdjSubjectSalaryDto(
            asj.id,
            asj.employee.id,
            asj.stdSalary,
            asj.finalStdSalary,
            asj.isPaybandApplied,
            asj.grade.id,
            asj.employee.empNum,
            asj.employee.name,
            asj.employee.department.name,
            pc.grade.name,
            asj.employee.positionName,
            asj.employee.rank.name,
            pc.upperBound
        )
        FROM AdjustSubject asj
        JOIN PaybandCriteria pc
            ON pc.grade.id = asj.grade.id
        WHERE asj.adjust.id = :adjustId
            AND pc.adjust.id = :adjustId
            AND asj.isSubject = true
            AND (
                asj.employee.name LIKE %:searchKey%
                OR asj.employee.empNum LIKE %:searchKey%
            )
            AND asj.deleted!=true AND pc.deleted!=true AND asj.isSubject=true
        """
    )
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndUpperWithSearchKey(Long adjustId, String searchKey);

    @Query("""
        SELECT new com.bongsco.api.adjust.common.dto.AdjSubjectSalaryDto(
            asj.id,
            asj.employee.id,
            asj.stdSalary,
            asj.finalStdSalary,
            asj.isPaybandApplied,
            asj.grade.id,
            asj.employee.empNum,
            asj.employee.name,
            asj.employee.department.name,
            pc.grade.name,
            asj.employee.positionName,
            asj.employee.rank.name,
            pc.lowerBound
        )
        FROM AdjustSubject asj
        JOIN PaybandCriteria pc ON pc.grade.id = asj.grade.id
        WHERE asj.adjust.id = :adjustId AND pc.adjust.id = :adjustId AND asj.isSubject = true
            AND (asj.employee.name LIKE %:searchKey% OR asj.employee.empNum LIKE %:searchKey%)
            AND asj.deleted!=true
            AND pc.deleted!=true
            AND asj.isSubject=true
        """
    )
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndLowerWithSearchKey(Long adjustId, String searchKey);

    @Query(
        value = """
             SELECT new com.bongsco.api.adjust.annual.dto.MainResultDto(
                 e.empNum, e.name, g.name, e.positionName, d.name, r.code,\s
                 e.stdSalaryIncrementRate, asj.finalStdSalary, asj.stdSalary,\s
                 asj.hpoBonus, asj.isInHpo, e.id, asj.id, g.id, r.id, ag.id, s.bonusMultiplier, s.salaryIncrementRate)
            FROM AdjustSubject asj
                JOIN Employee e ON e.id = asj.employee.id\s
                JOIN Grade g ON g.id = asj.grade.id
                JOIN Department d ON d.id = e.department.id
                JOIN Rank r ON r.id = asj.rank.id
                JOIN AdjustGrade ag ON ag.adjust.id = asj.adjust.id AND ag.grade.id = g.id
                JOIN SalaryIncrementByRank s ON s.adjustGrade.id = ag.id AND s.rank.id = r.id
            WHERE asj.adjust.id = :adjustId
                AND e.empNum LIKE COALESCE(:filterEmpNum, e.empNum)
                AND e.name LIKE COALESCE(:filterName, e.name)
                AND g.name = COALESCE(:filterGrade, g.name)
                AND d.name = COALESCE(:filterDepartment, d.name)
                AND r.code = COALESCE(:filterRank, r.code)
            """,
        countQuery = """
            SELECT COUNT(asj.id)
            FROM AdjustSubject asj
                JOIN Employee e ON e.id = asj.employee.id\s
                JOIN Grade g ON g.id = asj.grade.id
                JOIN Department d ON d.id = e.department.id
                JOIN Rank r ON r.id = asj.rank.id
                JOIN AdjustGrade ag ON ag.adjust.id = asj.adjust.id AND ag.grade.id = g.id
                JOIN SalaryIncrementByRank s ON s.adjustGrade.id = ag.id AND s.rank.id = r.id
            WHERE asj.adjust.id = :adjustId
                AND e.empNum LIKE COALESCE(:filterEmpNum, e.empNum)
                AND e.name LIKE COALESCE(:filterName, e.name)
                AND g.name = COALESCE(:filterGrade, g.name)
                AND d.name = COALESCE(:filterDepartment, d.name)
                AND r.code = COALESCE(:filterRank, r.code)
            """
    )
    Page<MainResultDto> findResultDtoWithPagination(
        @Param("adjustId") Long adjustId,
        @Param("filterEmpNum") String filterEmpNum,
        @Param("filterName") String filterName,
        @Param("filterGrade") String filterGrade,
        @Param("filterDepartment") String filterDepartment,
        @Param("filterRank") String filterRank,
        Pageable pageable);
}
