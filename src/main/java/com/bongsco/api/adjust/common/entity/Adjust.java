package com.bongsco.api.adjust.common.entity;

import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.api.adjust.common.domain.AdjustType;
import com.bongsco.api.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE adjust SET deleted = true WHERE id = ?")
public class Adjust extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AdjustType adjustType;

    @Column
    private Boolean isSubmitted;

    @Column(length = 50, nullable = false)
    private String author;

    @Column
    private Integer orderNumber;

    @Column
    private Double hpoSalaryIncrementRateByRank;

    @Column
    private Double hpoBonusMultiplier;

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

