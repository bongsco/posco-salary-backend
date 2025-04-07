package com.bongsco.api.adjust.annual.entity;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.adjust.common.entity.AdjustGrade;
import com.bongsco.api.common.entity.BaseEntity;
import com.bongsco.api.employee.entity.Grade;
import com.bongsco.api.employee.entity.Rank;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE salary_increment_rate_by_rank SET deleted = true WHERE id = ?")
public class SalaryIncrementRateByRank extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rank_id", nullable = false)
    private Rank rank;

    @ManyToOne
    @JoinColumn(name = "adjust_id", nullable = false)
    private Adjust adjust;

    @ManyToOne
    @JoinColumn(name = "adjust_grade_id", nullable = false)
    private AdjustGrade adjustGrade;

    @ManyToOne
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @Column
    private Double evalDiffIncrement;

    @Column
    private Double evalDiffBonus;
}