package com.bongsco.poscosalarybackend.user.domain;

import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.adjust.domain.PaymentCriteria;
import com.bongsco.poscosalarybackend.global.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE employee SET deleted = true WHERE id = ?")
public class Employee extends BaseEntity {
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

    @Column(length = 10, nullable = false, unique = true)
    private String empNum;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Column(length = 10)
    private LocalDate birth;

    @Column(length = 10)
    private String positionName;

    @Column(length = 10)
    private String positionArea;

    @Column
    private Double stdSalaryIncrementRate;
}

