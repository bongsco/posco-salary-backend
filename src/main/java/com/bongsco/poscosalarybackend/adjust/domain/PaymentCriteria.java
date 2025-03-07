package com.bongsco.poscosalarybackend.adjust.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "payment_criteria")
@Data
public class PaymentCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String paymentName;

    @OneToMany(mappedBy = "paymentCriteria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubjectCriteria> subjectCriteriaList;
}


