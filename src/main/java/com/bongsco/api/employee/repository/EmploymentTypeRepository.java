package com.bongsco.api.employee.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bongsco.api.adjust.annual.dto.response.SubjectCriteriaResponse;
import com.bongsco.api.employee.entity.EmploymentType;

public interface EmploymentTypeRepository extends JpaRepository<EmploymentType, Long> {

    @Query("""
            SELECT new com.bongsco.api.adjust.annual.dto.response.SubjectCriteriaResponse.SelectableItemDto(
                e.id,
                e.name,
                CASE WHEN e.id IN :selectedIds THEN true ELSE false END
            )
            FROM EmploymentType e
        """)
    List<SubjectCriteriaResponse.SelectableItemDto> findAllWithSelection(@Param("selectedIds") Set<Long> selectedIds);
}
