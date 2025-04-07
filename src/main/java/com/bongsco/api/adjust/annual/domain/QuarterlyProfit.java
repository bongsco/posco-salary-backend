package com.bongsco.api.adjust.annual.domain;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.api.global.domain.BaseEntity;
import com.bongsco.api.user.domain.Department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quarterly_profit")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE quarterly_profit SET deleted = true WHERE id = ?")
public class QuarterlyProfit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dep_id", nullable = false)
    private Department department;

    @Column(nullable = false)
    private Double goalProfit;

    @Column(nullable = false)
    private Double actualProfit;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer quarter;
}

