package com.bongsco.poscosalarybackend.adjust.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.poscosalarybackend.adjust.domain.AdjSubject;
import com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto;

@Repository
public interface AdjSubjectRepository extends JpaRepository<AdjSubject, Long> {
    List<AdjSubject> findByAdjInfo_Id(long id);

    @Query("SELECT asj FROM AdjSubject asj JOIN asj.employee e WHERE asj.adjInfo.id = :adjInfoId AND (e.empNum = :searchKey or e.name = :searchKey)")
    List<AdjSubject> findByAdjInfoIdAndEmployeeName(
        @Param("adjInfoId") long adjInfoId,
        @Param("searchKey") String searchKey
    );

    Optional<AdjSubject> findByAdjInfoIdAndEmployeeId(long adjInfoId, long employeeId);

    @Query("SELECT asj.id as adjSubjectId, asj.employee.id as employeeId, s.stdSalary, asj.paybandUse FROM AdjSubject asj JOIN Salary s ON asj.employee.id = s.employee.id  WHERE asj.adjInfo.id = :adjInfoId AND asj.subjectUse = true")
    List<AdjSubjectSalaryDto> findAllAdjSubjectAndStdSalary(@Param("adjInfoId") Long adjInfoId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AdjSubject asj SET asj.paybandUse=:paybandUse WHERE asj.id=:id")
    int updateAdjSubjectPaybandUse(@Param("id") Long id, @Param("paybandUse") Boolean paybandUse);
}
