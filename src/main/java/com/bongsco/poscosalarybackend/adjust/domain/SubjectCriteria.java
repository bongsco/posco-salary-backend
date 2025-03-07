package com.bongsco.poscosalarybackend.adjust.domain;

import com.bongsco.poscosalarybackend.user.domain.Station;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "subject_criteria")
@Data
public class SubjectCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;

    @ManyToOne
    @JoinColumn(name = "payment_criteria_id", nullable = false)
    private PaymentCriteria paymentCriteria;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    private LocalDate createdAt;
    private LocalDate baseDate;
    private LocalDate exceptionStartDate;
    private LocalDate exceptionEndDate;
    private LocalDate promotionStartDate;
    private LocalDate promotionEndDate;
}


