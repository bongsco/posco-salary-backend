package com.bongsco.poscosalarybackend.adjust.domain;

import org.hibernate.annotations.SQLDelete;

import com.bongsco.poscosalarybackend.global.domain.BaseEntity;
import com.bongsco.poscosalarybackend.user.domain.Station;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "station_adj_info")
@Getter
@Setter
@SQLDelete(sql = "UPDATE station_adj_info SET deleted = true WHERE station_id = ? AND adj_info_id = ?")
public class StationAdjInfo extends BaseEntity {

    @EmbeddedId
    private StationAdjInfoId id;

    @ManyToOne
    @MapsId("stationId")
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne
    @MapsId("adjInfoId")
    @JoinColumn(name = "adj_info_id", nullable = false)
    private AdjInfo adjInfo;
}

