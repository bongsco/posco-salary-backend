package com.bongsco.poscosalarybackend.adjust.domain;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;

@Entity
@Table(name = "detail_work_step")
@Data
@SQLDelete(sql = "UPDATE detail_work_step SET deleted = true WHERE id = ?")
public class DetailWorkStep extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;

    @ManyToOne
    @JoinColumn(name = "step", nullable = false)
    private step step;
}
