package com.bongsco.poscosalarybackend.user.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "department")
@Data
@SQLDelete(sql = "UPDATE department SET deleted = true WHERE id = ?")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String depName;

    @Column(length = 20, nullable = false)
    private String depCode;
}
