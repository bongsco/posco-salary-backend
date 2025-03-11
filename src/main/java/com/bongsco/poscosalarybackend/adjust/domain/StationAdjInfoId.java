package com.bongsco.poscosalarybackend.adjust.domain;

import java.io.Serializable;
import java.util.Objects;

import com.bongsco.poscosalarybackend.user.domain.Station;

import jakarta.persistence.Embeddable;

@Embeddable
public class StationAdjInfoId implements Serializable {
    private Station station;
    private Long adjInfo;

    public StationAdjInfoId() {
    }

    public StationAdjInfoId(Station station, Long adjInfo) {
        this.station = station;
        this.adjInfo = adjInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StationAdjInfoId that = (StationAdjInfoId)o;
        return Objects.equals(station, that.station) &&
            Objects.equals(adjInfo, that.adjInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station, adjInfo);
    }
}
