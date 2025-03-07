package com.bongsco.poscosalarybackend.user.domain;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "grade")
@Data
@SQLDelete(sql = "UPDATE grade SET deleted = true WHERE id = ?")
public class Grade extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String gradeName;

    @Column(precision = 20, scale = 2, nullable = false)
    private BigDecimal gradeBaseSalary;
}
