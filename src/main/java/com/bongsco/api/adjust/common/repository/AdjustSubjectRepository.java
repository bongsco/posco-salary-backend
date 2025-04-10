package com.bongsco.api.adjust.common.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.api.adjust.annual.dto.AdjustSubjectSalaryDto;
import com.bongsco.api.adjust.common.dto.AdjSubjectSalaryDto;
import com.bongsco.api.adjust.common.entity.AdjustSubject;

@Repository
public interface AdjustSubjectRepository extends JpaRepository<AdjustSubject, Long> {
    List<AdjustSubject> findByAdjust_Id(Long adjustId);

    List<AdjustSubject> findByAdjust_IdAndIsSubjectTrue(Long adjustId);

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

    @Query("SELECT s.employee.id FROM AdjustSubject s WHERE s.adjust.id = :adjustId")
    Set<Long> findEmployeeIdsByAdjustId(@Param("adjustId") Long adjustId);

    @Query("""
                  SELECT new com.bongsco.api.adjust.annual.dto.AdjustSubjectSalaryDto(
                  asj.employee.id,
                  asj.grade.baseSalary,
                  s.salaryIncrementRate,
                  s.bonusMultiplier,
                  asj.id,
                  asj.isInHpo
                  )
                  FROM AdjustSubject asj
                  JOIN AdjustGrade ag ON ag.grade.id = asj.grade.id AND asj.adjust.id = ag.adjust.id
                  JOIN SalaryIncrementByRank s ON asj.rank.id = s.rank.id AND ag.id = s.adjustGrade.id
                  WHERE asj.adjust.id = :adjustId
                  AND asj.isSubject = true
        """)
    List<AdjustSubjectSalaryDto> findDtoByAdjustId(@Param("adjustId") Long adjustId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
        """
                UPDATE AdjustSubject asj
                SET asj.stdSalary = :newStdSalary, asj.finalStdSalary = :newStdSalary, asj.hpoBonus = :newHpoBonus
                WHERE asj.id = :adjustSubjectId
                    AND asj.isSubject = true
            """
    )
    void saveById(
        @Param("adjustSubjectId") Long adjustSubjectId,
        @Param("newStdSalary") Double newStdSalary,
        @Param("newHpoBonus") Double newHpoBonus);
}
