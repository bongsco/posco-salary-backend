package com.bongsco.poscosalarybackend.user.domain;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "department")
@Getter
@Setter
@SQLDelete(sql = "UPDATE department SET deleted = true WHERE id = ?")
public class Department extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String depName;

    @Column(length = 20, nullable = false)
    private String depCode;
}
