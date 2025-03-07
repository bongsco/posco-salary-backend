package com.bongsco.poscosalarybackend.adjust.domain;

import com.bongsco.poscosalarybackend.global.domain.SalaryType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "adj_info")
@Data
public class AdjInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String adjName;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;

    @Column
    private String workStep;

    @Column
    private String interfaceUse;

    @Column
    private String remarks;

    @Column(nullable = false)
    private LocalDate creationTimestamp;

    @Column(length = 50, nullable = false)
    private String creator;

    private Integer orderNumber;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal evalAnnualSalaryIncrement;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal evalPerformProvideRate;
}

