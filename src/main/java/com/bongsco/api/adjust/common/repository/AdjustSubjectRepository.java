package com.bongsco.api.adjust.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.bongsco.api.adjust.common.dto.AdjustSubjectSalaryDto;
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
}
