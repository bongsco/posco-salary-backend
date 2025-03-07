package com.bongsco.poscosalarybackend.user.domain;

import com.bongsco.poscosalarybackend.adjust.domain.SubjectCriteria;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "station")
@Data
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String stationName;

    @OneToMany(mappedBy = "station")
    private List<SubjectCriteria> subjectCriteriaList;
}

