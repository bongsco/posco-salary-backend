package com.bongsco.api.adjust.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.common.entity.Adjust;

@Repository
public interface AdjustRepository extends JpaRepository<Adjust, Long> {
    // startYear 와 endYear 둘 다 있는 경우
    List<Adjust> findByYearBetween(Integer year, Integer year2);

    @Query("""
        SELECT adj
        FROM Adjust adj
        WHERE adj.adjustType='ANNUAL'
            AND adj.id < :id
        ORDER BY adj.id DESC
        """
    )
    List<Adjust> findLatestAdjustInfo(@Param("id") Long id);

    @Query("SELECT a FROM Adjust a WHERE a.id = :id")
    Optional<Adjust> findByIdIncludingDeleted(@Param("id") Long id);
}
