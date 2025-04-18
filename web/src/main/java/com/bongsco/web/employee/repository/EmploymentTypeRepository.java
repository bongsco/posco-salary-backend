package com.bongsco.web.employee.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bongsco.web.adjust.annual.dto.response.SelectableItemDto;
import com.bongsco.web.employee.entity.EmploymentType;

public interface EmploymentTypeRepository extends JpaRepository<EmploymentType, Long> {

    @Query("""
            SELECT new com.bongsco.web.adjust.annual.dto.response.SelectableItemDto(
                g.id,
                g.name,
                (g.id IN :selectedIds)
            )
            FROM Grade g
        """)
    List<SelectableItemDto> findAllWithSelection(@Param("selectedIds") Set<Long> selectedIds);
}
