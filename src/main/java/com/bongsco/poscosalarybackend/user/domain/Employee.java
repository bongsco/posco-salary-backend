package com.bongsco.poscosalarybackend.user.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bongsco.poscosalarybackend.adjust.domain.PaymentCriteria;

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
@Table(name = "employee")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dep_id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade;

    @ManyToOne
    @JoinColumn(name = "rank_id", nullable = false)
    private Rank rank;

    @ManyToOne
    @JoinColumn(name = "payment_criteria_id", nullable = false)
    private PaymentCriteria paymentCriteria;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(length = 10, nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private Long empNum;

    @Column(nullable = false)
    private LocalDate hireDate;

    private LocalDate birth;

    private String positionName;
    private String positionArea;

    @Column(precision = 5, scale = 2)
    private BigDecimal stdSalaryIncrementRate;
}

