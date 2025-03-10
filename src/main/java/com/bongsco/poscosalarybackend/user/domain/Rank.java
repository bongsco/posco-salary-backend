package com.bongsco.poscosalarybackend.user.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "rank")
@Data
@SQLDelete(sql = "UPDATE rank SET deleted = true WHERE id = ?")
public class Rank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String rankCode;

    @Column(length = 50, nullable = false)
    private String rankName;
}

