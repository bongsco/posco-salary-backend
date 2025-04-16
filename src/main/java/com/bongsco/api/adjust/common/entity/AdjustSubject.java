package com.bongsco.api.adjust.common.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.bongsco.api.common.entity.BaseEntity;
import com.bongsco.api.employee.entity.Employee;
import com.bongsco.api.employee.entity.Grade;
import com.bongsco.api.employee.entity.Rank;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
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
@SQLDelete(sql = "UPDATE adjust_subject SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class AdjustSubject extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "adjust_subject_seq")
    @SequenceGenerator(
        name = "adjust_subject_seq",              // generator 이름
        sequenceName = "adjust_subject_seq",      // DB 시퀀스 이름
        allocationSize = 100                      // 미리 당겨올 ID 개수 (batch 성능 영향!)
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "adjust_id", nullable = false)
    private Adjust adjust;

    @ManyToOne
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @ManyToOne
    @JoinColumn(name = "rank_id", nullable = false)
    private Rank rank;

    @Column(nullable = false)
    private Boolean isSubject;

    @Column
    private Boolean isInHpo;

    @Column
    @Enumerated(EnumType.STRING)
    private PaybandAppliedType isPaybandApplied;

    @Column
    private Double stdSalary;

    @Column
    private Double hpoBonus;

    @Column
    private Double finalStdSalary;
}

