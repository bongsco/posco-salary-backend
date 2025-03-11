package com.bongsco.poscosalarybackend.adjust.domain;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;
import com.bongsco.poscosalarybackend.user.domain.Grade;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "grade_adj_info")
@Getter
@Setter
@SQLDelete(sql = "UPDATE grade_adj_info SET deleted = 1 WHERE grade_id = ? AND adj_info_id = ?")
public class GradeAdjInfo extends BaseEntity {

    @EmbeddedId
    private GradeAdjInfoId id;

    @ManyToOne
    @MapsId("gradeId")
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @ManyToOne
    @MapsId("adjInfoId")
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;
}
