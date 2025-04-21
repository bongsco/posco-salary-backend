package com.bongsco.web.adjust.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bongsco.web.adjust.annual.dto.response.HpoSalaryInfo;
import com.bongsco.web.adjust.annual.dto.response.RateInfo;
import com.bongsco.web.adjust.common.domain.AdjustType;
import com.bongsco.web.adjust.common.entity.Adjust;
import com.bongsco.web.adjust.common.repository.reflection.AdjustItemProjection;

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

    @Query("""
        SELECT new com.bongsco.web.adjust.annual.dto.response.HpoSalaryInfo(
            adj.hpoSalaryIncrementByRank,
            adj.hpoBonusMultiplier
        )
        FROM Adjust adj
        WHERE adj.id = :adjustId
        """
    )
    HpoSalaryInfo findHpoSalaryInfoById(@Param("adjustId") Long adjustId);

    /* adjustGrade 테이블에 isActive가 True인 애들을 반환해야하는데, 아직 merge가 안되어서 이 부분은 나중에 수정 예정 */
    @Query("""
        SELECT new com.bongsco.web.adjust.annual.dto.response.RateInfo(
            g.name,
            r.code,
            sibr.salaryIncrementRate,
            sibr.bonusMultiplier
        )
        FROM SalaryIncrementByRank sibr
        JOIN sibr.adjustGrade ag
        JOIN ag.grade g
        JOIN sibr.rank r
        JOIN ag.adjust a
        WHERE a.id = :adjustId
        """)
    List<RateInfo> findRateInfoByAdjustId(Long adjustId);

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
            a.author,
            nps.url
        FROM
            adjust a
        LEFT JOIN (
            SELECT
                adjust_id,
                step_name,
                detail_step_name,
                url
            FROM (
                SELECT
                    rs.adjust_id,
                    s.name AS step_name,
                    s.detail_step_name,
                    s.url,
                    ROW_NUMBER() OVER (
                        PARTITION BY rs.adjust_id
                        ORDER BY
                            CASE
                                WHEN s.name = 'CRITERIA' THEN 1
                                WHEN s.name = 'PREPARATION' THEN 2
                                WHEN s.name = 'MAIN' THEN 3
                                ELSE 999
                            END,
                            s.order_number
                    ) AS rn
                FROM (
                    SELECT
                        adjust_id,
                        step_id
                    FROM
                        adjust_step
                    WHERE
                        is_done = false
                ) rs
                JOIN step s ON rs.step_id = s.id
                    ) ranked
                    WHERE rn = 1
            ) nps ON a.id = nps.adjust_id
            WHERE (COALESCE(array_length(CAST(:year AS integer[]), 1), 0) = 0 OR a.year = ANY(CAST(:year AS integer[])))
                AND (COALESCE(array_length(CAST(:month AS integer[]), 1), 0) = 0 OR a.month = ANY (CAST(:month AS integer[])))
                AND (COALESCE(array_length(CAST(:adjustType AS text[]), 1), 0) = 0 OR a.adjust_type = ANY (CAST(:adjustType AS text[])))
                AND (COALESCE(array_length(CAST(:author AS text[]), 1), 0) = 0 OR a.author LIKE ANY(CAST(:author AS text[])))
                AND (COALESCE(array_length(CAST(:isSubmitted AS boolean[]), 1), 0) = 0 OR a.is_submitted = ANY (CAST(:isSubmitted AS boolean[])))
                AND (
                        COALESCE(array_length(CAST(:state AS boolean[]), 1), 0) = 0
                        OR ((TRUE = ANY(CAST(:state AS boolean[]))) AND nps.step_name IS NULL)
                        OR ((FALSE = ANY(CAST(:state AS boolean[]))) AND nps.step_name IS NOT NULL)
                )        
        """,
        countQuery = """
            SELECT count(*)
                    FROM
            adjust a
        LEFT JOIN (
            SELECT
                adjust_id,
                step_name,
                detail_step_name,
                url
            FROM (
                SELECT
                    rs.adjust_id,
                    s.name AS step_name,
                    s.detail_step_name,
                    s.url,
                    ROW_NUMBER() OVER (
                        PARTITION BY rs.adjust_id
                        ORDER BY
                            CASE
                                WHEN s.name = 'CRITERIA' THEN 1
                                WHEN s.name = 'PREPARATION' THEN 2
                                WHEN s.name = 'MAIN' THEN 3
                                ELSE 999
                            END,
                            s.order_number
                    ) AS rn
                FROM (
                    SELECT
                        adjust_id,
                        step_id
                    FROM
                        adjust_step
                    WHERE
                        is_done = false
                ) rs
                JOIN step s ON rs.step_id = s.id
                    ) ranked
                    WHERE rn = 1
            ) nps ON a.id = nps.adjust_id
            WHERE (COALESCE(array_length(CAST(:year AS integer[]), 1), 0) = 0 OR a.year = ANY(CAST(:year AS integer[])))
                AND (COALESCE(array_length(CAST(:month AS integer[]), 1), 0) = 0 OR a.month = ANY (CAST(:month AS integer[])))
                AND (COALESCE(array_length(CAST(:adjustType AS text[]), 1), 0) = 0 OR a.adjust_type = ANY (CAST(:adjustType AS text[])))
                AND (COALESCE(array_length(CAST(:author AS text[]), 1), 0) = 0 OR a.author LIKE ANY(CAST(:author AS text[])))
                AND (COALESCE(array_length(CAST(:isSubmitted AS boolean[]), 1), 0) = 0 OR a.is_submitted = ANY (CAST(:isSubmitted AS boolean[])))
                AND (
                        COALESCE(array_length(CAST(:state AS boolean[]), 1), 0) = 0
                        OR ((TRUE = ANY(CAST(:state AS boolean[]))) AND nps.step_name IS NULL)
                        OR ((FALSE = ANY(CAST(:state AS boolean[]))) AND nps.step_name IS NOT NULL)
                )
            """,
        nativeQuery = true)
    Page<AdjustItemProjection> findAdjustItemsWithNextStepPaginated(
        @Param("year") Integer[] year, @Param("month") Integer[] month,
        @Param("adjustType") String[] adjustType, @Param("state") Boolean[] state,
        @Param("isSubmitted") Boolean[] isSubmitted, @Param("author") String[] author, Pageable pageable);

    @Query("SELECT MAX(a.orderNumber) FROM Adjust a WHERE a.year = :year AND a.adjustType = :adjustType")
    Integer findMaxOrderNumberByYear(@Param("year") int year, @Param("adjustType") AdjustType adjustType);
}
