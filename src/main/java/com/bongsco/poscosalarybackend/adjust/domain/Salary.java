package com.bongsco.poscosalarybackend.adjust.domain;

import com.bongsco.poscosalarybackend.user.domain.Employee;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "salary")
@Data
@SQLDelete(sql = "UPDATE salary SET deleted = true WHERE adj_info_id = ? AND employee_id = ?")
public class Salary {
    @Id
    @ManyToOne
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;

    @Id
    @ManyToOne
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

