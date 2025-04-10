package com.bongsco.api.adjust.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.annual.dto.response.HpoSalaryInfo;
import com.bongsco.api.adjust.annual.dto.response.RateInfo;
import com.bongsco.api.adjust.common.dto.AdjSubjectSalaryDto;
import com.bongsco.api.adjust.common.entity.Adjust;

@Repository
public interface AdjustRepository extends JpaRepository<Adjust, Long> {
    // startYear 와 endYear 둘 다 있는 경우
    List<Adjust> findByYearBetween(Integer year, Integer year2);

    @Query("""
        SELECT adj
        FROM Adjust adj
        WHERE adj.adjustType='ANNUAL'
            AND adj.id < :id
        ORDER BY adj.id DESC
        """
    )
    List<Adjust> findLatestAdjustInfo(@Param("id") Long id);

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
        SELECT new com.bongsco.api.adjust.annual.dto.response.HpoSalaryInfo(
            adj.hpoSalaryIncrementByRank,
            adj.hpoBonusMultiplier
        )
        FROM Adjust adj
        WHERE adj.id = :adjustId
        """
    )
    HpoSalaryInfo findHpoSalaryInfoById(@Param("adjustId") Long adjustId);

    /* adjustGrade 테이블에 isActive가 True인 애들을 반환해야하는데, 아직 merge가 안되어서 이 부분은 나중에 수정 예정 */
    @Query("""
        SELECT new com.bongsco.api.adjust.annual.dto.response.RateInfo(
            g.name,
            r.code,
            sibr.salaryIncrementRate,
            sibr.bonusMultiplier
        )
        FROM SalaryIncrementByRank sibr
        JOIN sibr.adjustGrade ag
        JOIN ag.grade g
        JOIN sibr.rank r
        JOIN ag.adjust a
        WHERE a.id = :adjustId
        """)
    List<RateInfo> findRateInfoByAdjustId(Long adjustId);

    @Query("SELECT a FROM Adjust a WHERE a.id = :id")
    Optional<Adjust> findByIdIncludingDeleted(@Param("id") Long id);
}
