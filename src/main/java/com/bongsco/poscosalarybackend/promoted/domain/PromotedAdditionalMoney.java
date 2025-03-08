package com.bongsco.poscosalarybackend.promoted.domain;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;
import com.bongsco.poscosalarybackend.global.domain.Status;
import com.bongsco.poscosalarybackend.user.domain.Grade;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "promoted_additional_money")
@Data
public class PromotedAdditionalMoney {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @ManyToOne
    @JoinColumn(name = "adj_id", nullable = false)
    private AdjInfo adjInfo;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(precision = 20, scale = 2)
    private BigDecimal monthlyAdditionalMoney;
}

