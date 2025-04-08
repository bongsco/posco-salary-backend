package com.bongsco.api.adjust.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
