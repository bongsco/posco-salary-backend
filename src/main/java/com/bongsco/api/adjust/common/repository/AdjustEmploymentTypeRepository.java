package com.bongsco.api.adjust.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bongsco.api.adjust.common.entity.AdjustEmploymentType;
public interface AdjustEmploymentTypeRepository extends JpaRepository<AdjustEmploymentType, Long> {
    @Query("""
            SELECT ae FROM AdjustEmploymentType ae
            JOIN FETCH ae.employmentType
            WHERE ae.adjust.id = :adjustId
        """)
    List<AdjustEmploymentType> findByAdjustId(@Param("adjustId") Long adjustId);

    List<AdjustEmploymentType> findByAdjustIdAndIsActive(Long adjustId, boolean isActive);
}
