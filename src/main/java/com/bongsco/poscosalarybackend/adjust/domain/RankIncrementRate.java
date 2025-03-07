package com.bongsco.poscosalarybackend.adjust.domain;

import com.bongsco.poscosalarybackend.user.domain.Grade;
import com.bongsco.poscosalarybackend.user.domain.Rank;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;

@Entity
@Table(name = "rank_increment_rate")
@Data
@SQLDelete(sql = "UPDATE rank_increment_rate SET deleted = true WHERE rank_id = ? AND adj_info_id = ? AND grade_id = ?ß")
public class RankIncrementRate {
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

