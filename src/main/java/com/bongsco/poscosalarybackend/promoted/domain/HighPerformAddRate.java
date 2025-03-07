package com.bongsco.poscosalarybackend.promoted.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "high_perform_add_rate")
@Data
public class HighPerformAddRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 5, scale = 2)
    private BigDecimal evalAnnualSalaryIncrement;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal evalPerformProvideRate;
}
