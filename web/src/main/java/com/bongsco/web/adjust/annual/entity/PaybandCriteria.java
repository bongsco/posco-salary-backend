package com.bongsco.web.adjust.annual.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.bongsco.web.adjust.common.entity.Adjust;
import com.bongsco.web.common.entity.BaseEntity;
import com.bongsco.web.employee.entity.Grade;
import com.bongsco.web.adjust.common.entity.AdjustGrade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@SQLRestriction("deleted = false")
public class PaybandCriteria extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "adjust_grade_id", nullable = false)
    private AdjustGrade adjustGrade;

    @Column(nullable = false)
    private Double upperBound;

    @Column(nullable = false)
    private Double lowerBound;

    private Double upperBoundMemo;

    private Double lowerBoundMemo;

}

