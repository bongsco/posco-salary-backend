package com.bongsco.poscosalarybackend.adjust.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.poscosalarybackend.adjust.domain.AdjSubject;
import com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto;

@Repository
public interface AdjSubjectRepository extends JpaRepository<AdjSubject, Long> {
    List<AdjSubject> findByAdjInfo_Id(Long id);

    @Query("SELECT asj FROM AdjSubject asj "
        + "WHERE asj.adjInfo.id < :adjInfoId "
        + "AND asj.employee.id = :employeeId "
        + "AND  asj.deleted != true "
        + "ORDER BY asj.adjInfo.id DESC LIMIT 1")
    AdjSubject findBeforeAdjSubject(Long adjInfoId, Long employeeId);

    Optional<AdjSubject> findById(Long id);

    @Query("SELECT asj FROM AdjSubject asj JOIN asj.employee e WHERE asj.adjInfo.id = :adjInfoId "
        + "AND (e.empNum LIKE %:searchKey% OR e.name LIKE %:searchKey%)"
        + "AND asj.deleted!=true AND e.deleted!=true")
    List<AdjSubject> findByAdjInfoIdAndEmployeeName(
        @Param("adjInfoId") Long adjInfoId,
        @Param("searchKey") String searchKey
    );

    Optional<AdjSubject> findByAdjInfoIdAndEmployeeId(Long adjInfoId, Long employeeId);

    @Query(
        "SELECT new com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto(asj.id, asj.employee.id, asj.stdSalary, asj.paybandUse, asj.grade.id, asj.rank.rankName, pc.upperLimitPrice, pc.grade.gradeName) "
            + "FROM AdjSubject asj "
            + "JOIN PaybandCriteria pc ON pc.grade.id = asj.grade.id "
            + "WHERE asj.adjInfo.id = :adjInfoId AND pc.adjInfo.id = :adjInfoId AND asj.subjectUse = true "
            + "AND asj.deleted!=true AND pc.deleted!=true AND asj.subjectUse=true")
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndUpper(@Param("adjInfoId") Long adjInfoId);

    @Query(
        "SELECT new com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto(asj.id, asj.employee.id, asj.stdSalary, asj.paybandUse, asj.grade.id, asj.rank.rankName, pc.lowerLimitPrice, pc.grade.gradeName) "
            + "FROM AdjSubject asj "
            + "JOIN PaybandCriteria pc ON pc.grade.id = asj.grade.id "
            + "WHERE asj.adjInfo.id = :adjInfoId AND pc.adjInfo.id = :adjInfoId AND asj.subjectUse = true "
            + "AND asj.deleted!=true AND pc.deleted!=true AND asj.subjectUse=true")
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndLower(@Param("adjInfoId") Long adjInfoId);

    @Query(
        "SELECT new com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto(asj.id, asj.employee.id, asj.stdSalary, asj.paybandUse, asj.grade.id, asj.rank.rankName, pc.upperLimitPrice, pc.grade.gradeName) "
            + "FROM AdjSubject asj "
            + "JOIN PaybandCriteria pc ON pc.grade.id = asj.grade.id "
            + "WHERE asj.adjInfo.id = :adjInfoId AND pc.adjInfo.id = :adjInfoId AND asj.subjectUse = true "
            + "AND (asj.employee.name LIKE %:searchKey% OR asj.employee.empNum LIKE %:searchKey%) "
            + "AND asj.deleted!=true AND pc.deleted!=true AND asj.subjectUse=true")
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndUpperWithSearchKey(Long adjInfoId, String searchKey);

    @Query(
        "SELECT new com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto(asj.id, asj.employee.id, asj.stdSalary, asj.paybandUse, asj.grade.id, asj.rank.rankName, pc.lowerLimitPrice, pc.grade.gradeName) "
            + "FROM AdjSubject asj "
            + "JOIN PaybandCriteria pc ON pc.grade.id = asj.grade.id "
            + "WHERE asj.adjInfo.id = :adjInfoId AND pc.adjInfo.id = :adjInfoId AND asj.subjectUse = true "
            + "AND (asj.employee.name LIKE %:searchKey% OR asj.employee.empNum LIKE %:searchKey%) "
            + "AND asj.deleted!=true AND pc.deleted!=true AND asj.subjectUse=true")
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndLowerWithSearchKey(Long adjInfoId, String searchKey);
}
