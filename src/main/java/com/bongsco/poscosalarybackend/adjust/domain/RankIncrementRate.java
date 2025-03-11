package com.bongsco.poscosalarybackend.adjust.domain;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;
import com.bongsco.poscosalarybackend.user.domain.Grade;
import com.bongsco.poscosalarybackend.user.domain.Rank;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rank_increment_rate")
@Getter
@Setter
@SQLDelete(sql = "UPDATE rank_increment_rate SET deleted = true WHERE rank_id = ? AND adj_info_id = ? AND grade_id = ?ß")
public class RankIncrementRate extends BaseEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "rank_id", nullable = false)
    private Rank rank;

    @Id
    @ManyToOne
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;

    @Id
    @ManyToOne
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @Column(precision = 5, scale = 2)
    private BigDecimal evalDiffIncrement;

    @Column(precision = 5, scale = 2)
    private BigDecimal evalDiffBonus;
}

