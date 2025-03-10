package com.bongsco.poscosalarybackend.adjust.domain;

import com.bongsco.poscosalarybackend.user.domain.Department;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;

@Entity
@Table(name = "quarterly_profit")
@Data
@SQLDelete(sql = "UPDATE quarterly_profit SET deleted = true WHERE id = ?")
public class QuarterlyProfit {
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

