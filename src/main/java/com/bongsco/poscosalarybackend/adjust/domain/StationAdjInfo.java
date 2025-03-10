package com.bongsco.poscosalarybackend.adjust.domain;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;
import com.bongsco.poscosalarybackend.user.domain.Station;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "station_adj_info")
@Data
@SQLDelete(sql = "UPDATE station_adj_info SET deleted = true WHERE station_id = ? AND adj_info_id = ?")
public class StationAdjInfo extends BaseEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Id
    @ManyToOne
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;
}
