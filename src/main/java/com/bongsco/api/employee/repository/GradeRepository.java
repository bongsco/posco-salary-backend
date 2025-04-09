package com.bongsco.api.employee.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bongsco.api.adjust.annual.dto.response.SelectableItemDto;
import com.bongsco.api.employee.entity.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    @Query("""
            SELECT new com.bongsco.api.adjust.annual.dto.response.SelectableItemDto(
                g.id,
                g.name,
                CASE WHEN g.id IN :selectedIds THEN true ELSE false END
            )
            FROM Grade g
        """)
    List<SelectableItemDto> findAllWithSelection(@Param("selectedIds") Set<Long> selectedIds);
}
