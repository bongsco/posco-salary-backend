package com.bongsco.poscosalarybackend.adjust.domain;

import com.bongsco.poscosalarybackend.user.domain.Employee;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "adj_subject")
@Data
public class AdjSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "adj_id", nullable = false)
    private AdjInfo adjInfo;

    @Column(length = 1)
    private String subjectUse;

    @Column(length = 1)
    private String inHighPerformGroup;

    @Column(length = 1)
    private String paybandUse;
}

