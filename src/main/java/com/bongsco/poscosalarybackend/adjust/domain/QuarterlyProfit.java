package com.bongsco.poscosalarybackend.adjust.domain;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;
import com.bongsco.poscosalarybackend.user.domain.Department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "quarterly_profit")
@Data
@SQLDelete(sql = "UPDATE quarterly_profit SET deleted = true WHERE id = ?")
public class QuarterlyProfit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dep_id", nullable = false)
    private Department department;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal goalProfit;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal actualProfit;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer quarter;
}
