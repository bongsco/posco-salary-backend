package com.bongsco.poscosalarybackend.user.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "station")
@Data
@SQLDelete(sql = "UPDATE station SET deleted = true WHERE id = ?")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String stationName;

}

