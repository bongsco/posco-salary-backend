package com.bongsco.web.adjust.annual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.web.adjust.annual.entity.PaybandCriteria;

@Repository
public interface PaybandCriteriaRepository extends JpaRepository<PaybandCriteria, Long> {

    // ✅ 일반 메서드
    List<PaybandCriteria> findByAdjustId(Long id);

    // ✅ baseSalary * (upper/lowerBound / 100.0) 계산된 limit 조회
    @Query("""
            SELECT 
                (g.baseSalary * (pc.upperBound / 100.0)) AS upperLimit,
                (g.baseSalary * (pc.lowerBound / 100.0)) AS lowerLimit
            FROM PaybandCriteria pc
            JOIN pc.grade g
            WHERE pc.adjust.id = :adjustId
              AND g.id = :gradeId
        """)
    Optional<PaybandLimitInfo> findLimitInfo(
        @Param("adjustId") Long adjustId,
        @Param("gradeId") Long gradeId
    );

    List<PaybandCriteria> findByAdjustIdAndIsActiveTrue(Long adjustId);

    // ✅ 내부 인터페이스: 상한/하한 limit 계산 Projection
    interface PaybandLimitInfo {
        Double getUpperLimit(); // 상한 금액

        Double getLowerLimit(); // 하한 금액
    }
}
