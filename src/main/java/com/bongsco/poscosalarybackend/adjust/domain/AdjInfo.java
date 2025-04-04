package com.bongsco.poscosalarybackend.adjust.domain;

import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.AdjType;
import com.bongsco.poscosalarybackend.global.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "adj_info")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE adj_info SET deleted = true WHERE id = ?")
public class AdjInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AdjType adjType;

    @Column(length = 4)
    private String workStep;

    @Column
    private Boolean interfaceUse;

    @Column(length = 4000)
    private String remarks;

    @Column(nullable = false)
    private LocalDate creationTimestamp;

    @Column(length = 50, nullable = false)
    private String creator;

    @Column
    private Integer orderNumber;

    @Column
    private Double evalAnnualSalaryIncrement;

    @Column
    private Double evalPerformProvideRate;

    @Column
    private LocalDate baseDate;

    @Column
    private LocalDate exceptionStartDate;

    @Column
    private LocalDate exceptionEndDate;

    @Column
    private LocalDate promotionStartDate;

    @Column
    private LocalDate promotionEndDate;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;
}

