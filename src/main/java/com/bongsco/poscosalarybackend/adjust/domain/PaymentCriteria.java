package com.bongsco.poscosalarybackend.adjust.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Entity
@Table(name = "payment_criteria")
@Data
@SQLDelete(sql = "UPDATE payment_criteria SET deleted = true WHERE id = ?")
public class PaymentCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String paymentName;
}


