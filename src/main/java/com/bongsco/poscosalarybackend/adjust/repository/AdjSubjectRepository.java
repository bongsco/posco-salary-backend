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
    List<AdjSubject> findByAdjInfo_Id(long id);

    @Query("SELECT asj FROM AdjSubject asj JOIN asj.employee e WHERE asj.adjInfo.id = :adjInfoId "
        + "AND (e.empNum LIKE %:searchKey% OR e.name LIKE %:searchKey%)")
    List<AdjSubject> findByAdjInfoIdAndEmployeeName(
        @Param("adjInfoId") long adjInfoId,
        @Param("searchKey") String searchKey
    );

    Optional<AdjSubject> findByAdjInfoIdAndEmployeeId(long adjInfoId, long employeeId);

    @Query(
        "SELECT new com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto(asj.id, asj.employee.id, s.stdSalary, asj.paybandUse, asj.employee.grade.id, pc.upperLimitPrice, pc.grade.gradeName) "
            + "FROM AdjSubject asj "
            + "JOIN Salary s ON asj.employee.id = s.employee.id  "
            + "JOIN PaybandCriteria pc ON pc.grade.id = s.grade.id "
            + "WHERE asj.adjInfo.id = :adjInfoId  AND s.adjInfo.id = :adjInfoId AND pc.adjInfo.id = :adjInfoId AND asj.subjectUse = true")
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndUpper(@Param("adjInfoId") Long adjInfoId);

    @Query(
        "SELECT new com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto(asj.id, asj.employee.id, s.stdSalary, asj.paybandUse, asj.employee.grade.id, pc.lowerLimitPrice, pc.grade.gradeName) "
            + "FROM AdjSubject asj "
            + "JOIN Salary s ON asj.employee.id = s.employee.id  "
            + "JOIN PaybandCriteria pc ON pc.grade.id = s.grade.id "
            + "WHERE asj.adjInfo.id = :adjInfoId  AND s.adjInfo.id = :adjInfoId AND pc.adjInfo.id = :adjInfoId AND asj.subjectUse = true")
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndLower(@Param("adjInfoId") Long adjInfoId);

    @Query(
        "SELECT new com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto(asj.id, asj.employee.id, s.stdSalary, asj.paybandUse, asj.employee.grade.id, pc.upperLimitPrice, pc.grade.gradeName) "
            + "FROM AdjSubject asj "
            + "JOIN Salary s ON asj.employee.id = s.employee.id  "
            + "JOIN PaybandCriteria pc ON pc.grade.id = s.grade.id "
            + "WHERE asj.adjInfo.id = :adjInfoId  AND s.adjInfo.id = :adjInfoId AND pc.adjInfo.id = :adjInfoId AND asj.subjectUse = true "
            + "AND (asj.employee.name LIKE %:searchKey% OR asj.employee.empNum LIKE %:searchKey%)")
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndUpperWithSearchKey(Long adjInfoId, String searchKey);

    @Query(
        "SELECT new com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto(asj.id, asj.employee.id, s.stdSalary, asj.paybandUse, asj.employee.grade.id, pc.upperLimitPrice, pc.grade.gradeName) "
            + "FROM AdjSubject asj "
            + "JOIN Salary s ON asj.employee.id = s.employee.id  "
            + "JOIN PaybandCriteria pc ON pc.grade.id = s.grade.id "
            + "WHERE asj.adjInfo.id = :adjInfoId  AND s.adjInfo.id = :adjInfoId AND pc.adjInfo.id = :adjInfoId AND asj.subjectUse = true "
            + "AND (asj.employee.name LIKE %:searchKey% OR asj.employee.empNum LIKE %:searchKey%)")
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalaryAndLowerWithSearchKey(Long adjInfoId, String searchKey);
}
