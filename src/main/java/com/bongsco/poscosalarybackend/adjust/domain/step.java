package com.bongsco.poscosalarybackend.adjust.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "step")
@Data
@SQLDelete(sql = "UPDATE step SET deleted = true WHERE id = ?")
public class step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stepName;
}
