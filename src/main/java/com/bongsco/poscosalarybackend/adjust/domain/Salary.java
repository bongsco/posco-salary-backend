package com.bongsco.poscosalarybackend.adjust.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;
import com.bongsco.poscosalarybackend.user.domain.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "salary")
@Getter
@Setter
@SQLDelete(sql = "UPDATE salary SET deleted = 1 WHERE adj_info_id = ? AND employee_id = ?")
public class Salary extends BaseEntity {

    @EmbeddedId
    private SalaryId id;

    @ManyToOne
    @MapsId("adjInfoId")
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;

    @ManyToOne
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal stdSalary;

    @Column(precision = 10, scale = 2)
    private BigDecimal performAddPayment;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;
}
