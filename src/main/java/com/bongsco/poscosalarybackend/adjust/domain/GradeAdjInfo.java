package com.bongsco.poscosalarybackend.adjust.domain;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;
import com.bongsco.poscosalarybackend.user.domain.Grade;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "grade_adj_info")
@Data
@SQLDelete(sql = "UPDATE grade_adj_info SET deleted = true WHERE grade_id = ? AND adj_info_id = ?")
public class GradeAdjInfo extends BaseEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @Id
    @ManyToOne
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;
}
