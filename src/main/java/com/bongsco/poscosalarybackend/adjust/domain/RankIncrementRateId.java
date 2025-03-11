package com.bongsco.poscosalarybackend.adjust.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class RankIncrementRateId implements Serializable {

    private Long rankId;
    private Long adjInfoId;
    private Long gradeId;

    public RankIncrementRateId() {
    }

    public RankIncrementRateId(Long rankId, Long adjInfoId, Long gradeId) {
        this.rankId = rankId;
        this.adjInfoId = adjInfoId;
        this.gradeId = gradeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RankIncrementRateId that = (RankIncrementRateId)o;
        return Objects.equals(rankId, that.rankId) &&
            Objects.equals(adjInfoId, that.adjInfoId) &&
            Objects.equals(gradeId, that.gradeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rankId, adjInfoId, gradeId);
    }
}

