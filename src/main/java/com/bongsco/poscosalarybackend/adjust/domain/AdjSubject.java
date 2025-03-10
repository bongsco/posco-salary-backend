package com.bongsco.poscosalarybackend.adjust.domain;

import com.bongsco.poscosalarybackend.user.domain.Employee;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "adj_subject")
@Data
@SQLDelete(sql = "UPDATE adj_subject SET deleted = true WHERE id = ?")
public class AdjSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;

    @Column(nullable = false)
    private Boolean subjectUse;

    @Column
    private Boolean inHighPerformGroup;

    @Column
    private Boolean paybandUse;
}

