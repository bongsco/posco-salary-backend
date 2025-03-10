package com.bongsco.poscosalarybackend.adjust.domain;

import com.bongsco.poscosalarybackend.user.domain.Grade;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "grade_adj_info")
@Data
@SQLDelete(sql = "UPDATE grade_adj_info SET deleted = true WHERE grade_id = ? AND adj_info_id = ?")
public class GradeAdjInfo {
    @Id
    @ManyToOne
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @Id
    @ManyToOne
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;
}
