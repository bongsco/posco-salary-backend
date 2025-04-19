package com.bongsco.mobile.repository;

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

import com.bongsco.mobile.dto.response.BarChartResponse;
import com.bongsco.mobile.entity.AdjustSubject;

@Repository
public interface AdjustSubjectRepository extends JpaRepository<AdjustSubject, Long> {

    @Query("""
        SELECT new com.bongsco.mobile.dto.response.BarChartResponse(
            a.year, a.orderNumber, asj.finalStdSalary, asj.hpoBonus, (COALESCE(asj.finalStdSalary, 0.0) + COALESCE(asj.grade.baseSalary, 0.0)) / 24.0 + 12.0
        )
        FROM AdjustSubject asj
        JOIN Adjust a ON asj.adjust.id = a.id
        WHERE asj.employee.id = :employeeId
            AND a.isSubmitted = true
        ORDER BY asj.id DESC
        LIMIT 5
    """)
    List<BarChartResponse> findFiveRecentBarchartData(@Param("employeeId") Long employeeId);
}
