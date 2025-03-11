package com.bongsco.poscosalarybackend.baseup.domain;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.adjust.domain.AdjInfo;
import com.bongsco.poscosalarybackend.global.domain.BaseEntity;
import com.bongsco.poscosalarybackend.global.domain.Status;
import com.bongsco.poscosalarybackend.user.domain.Grade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "baseup_criteria")
@Getter
@Setter
@SQLDelete(sql = "UPDATE baseup_criteria SET deleted = true WHERE id = ?")
public class BaseupCriteria extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;

    @ManyToOne
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(precision = 20, scale = 2)
    private BigDecimal fixedAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal fixedRate;
}
