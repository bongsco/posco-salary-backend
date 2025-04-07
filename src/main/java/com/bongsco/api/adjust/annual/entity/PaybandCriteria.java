package com.bongsco.api.adjust.annual.entity;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.api.adjust.common.entity.Adjust;
import com.bongsco.api.common.entity.BaseEntity;
import com.bongsco.api.employee.entity.Grade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@SQLDelete(sql = "UPDATE payband_criteria SET deleted = true WHERE id = ?")
public class PaybandCriteria extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "adjust_id", nullable = false)
    private Adjust adjust;

    @ManyToOne
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @Column
    private Double upperBound;

    @Column
    private Double lowerBound;
}

