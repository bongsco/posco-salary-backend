package com.bongsco.poscosalarybackend.user.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;

@Entity
@Table(name = "grade")
@Data
@SQLDelete(sql = "UPDATE grade SET deleted = true WHERE id = ?")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String gradeName;

    @Column(precision = 20, scale = 2, nullable = false)
    private BigDecimal gradeBaseSalary;
}

