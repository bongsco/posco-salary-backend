package com.bongsco.api.adjust.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.api.adjust.common.domain.AdjustType;
import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.adjust.common.repository.reflection.AdjustItemProjection;

@Repository
public interface AdjustRepository extends JpaRepository<Adjust, Long> {

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

    @Query(value = """
        SELECT
            a.id,
            a.year,
            a.month,
            a.adjust_type,
            a.order_number,
            nps.step_name,
            nps.detail_step_name,
            a.is_submitted,
            a.base_date ,
            a.start_date,
            a.end_date,
            a.author
        FROM
            adjust a
        LEFT JOIN (
            SELECT
                rs.adjust_id,
                s.name AS step_name,
                s.detail_step_name
            FROM (
                SELECT
                    adjust_id,
                    step_id,
                    ROW_NUMBER() OVER(PARTITION BY adjust_id ORDER BY step_id ASC) as rn
                FROM
                    adjust_step
                WHERE
                    is_done = false
            ) rs
            JOIN
                step s ON rs.step_id = s.id
            WHERE
                rs.rn = 1
        ) nps ON a.id = nps.adjust_id
        WHERE (a.year = COALESCE(:year, a.year))
            AND (a.month = COALESCE(:month, a.month))
            AND (a.is_submitted = COALESCE(:isSubmitted, a.is_submitted))
            AND (:state IS NULL OR (:state = TRUE AND nps.step_name IS NULL) OR (:state = FALSE AND nps.step_name IS NOT NULL))
            AND (:adjustType IS NULL OR a.adjust_type = :adjustType)
            AND (:author IS NULL OR a.author LIKE CONCAT('%', :author, '%'))
        """,
        countQuery = """
            SELECT count(*)
            FROM
                adjust a
            LEFT JOIN (
                SELECT
                    rs.adjust_id,
                    s.name AS step_name,
                    s.detail_step_name
                FROM (
                    SELECT
                        adjust_id,
                        step_id,
                        ROW_NUMBER() OVER(PARTITION BY adjust_id ORDER BY step_id ASC) as rn
                    FROM
                        adjust_step
                    WHERE
                        is_done = false
                ) rs
                JOIN
                    step s ON rs.step_id = s.id
                WHERE
                    rs.rn = 1
            ) nps ON a.id = nps.adjust_id
            WHERE (a.year = COALESCE(:year, a.year))
                AND (a.month = COALESCE(:month, a.month))
                AND (a.is_submitted = COALESCE(:isSubmitted, a.is_submitted))
                AND (:state IS NULL OR (:state = TRUE AND nps.step_name IS NULL) OR (:state = FALSE AND nps.step_name IS NOT NULL))
                AND (:adjustType IS NULL OR a.adjust_type = :adjustType)
                AND (:author IS NULL OR a.author LIKE CONCAT('%', :author, '%'))
            """,
        nativeQuery = true)
    Page<AdjustItemProjection> findAdjustItemsWithNextStepPaginated(
        @Param("year") Integer year, @Param("month") Integer month,
        @Param("adjustType") String adjustType, @Param("state") Boolean state,
        @Param("isSubmitted") Boolean isSubmitted, @Param("author") String author, Pageable pageable);

    @Query("SELECT MAX(a.orderNumber) FROM Adjust a WHERE a.year = :year AND a.adjustType = :adjustType")
    Integer findMaxOrderNumberByYear(@Param("year") int year, @Param("adjustType") AdjustType adjustType);
}
