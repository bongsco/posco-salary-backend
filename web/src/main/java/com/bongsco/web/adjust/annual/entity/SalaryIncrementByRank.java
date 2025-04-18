package com.bongsco.web.adjust.annual.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.bongsco.web.adjust.common.entity.AdjustGrade;
import com.bongsco.web.common.entity.BaseEntity;
import com.bongsco.web.employee.entity.Rank;

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
@SQLDelete(sql = "UPDATE salary_increment_by_rank SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class SalaryIncrementByRank extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rank_id", nullable = false)
    private Rank rank;

    @ManyToOne
    @JoinColumn(name = "adjust_grade_id", nullable = false)
    private AdjustGrade adjustGrade;

    @Column
    private Double salaryIncrementRate;

    @Column
    private Double bonusMultiplier;
}