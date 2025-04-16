package com.bongsco.web.employee.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bongsco.web.adjust.annual.dto.response.SelectableItemDto;
import com.bongsco.web.employee.entity.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByName(String name);

    @Query("""
            SELECT new com.bongsco.web.adjust.annual.dto.response.SelectableItemDto(
                g.id,
                g.name,
                CASE WHEN g.id IN :selectedIds THEN true ELSE false END
            )
            FROM Grade g
        """)
    List<SelectableItemDto> findAllWithSelection(@Param("selectedIds") Set<Long> selectedIds);
}
