package com.bongsco.poscosalarybackend.adjust.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class GradeAdjInfoId implements Serializable {

    private Long gradeId;
    private Long adjInfoId;

    public GradeAdjInfoId() {
    }

    public GradeAdjInfoId(Long gradeId, Long adjInfoId) {
        this.gradeId = gradeId;
        this.adjInfoId = adjInfoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GradeAdjInfoId that = (GradeAdjInfoId)o;
        return Objects.equals(gradeId, that.gradeId) &&
            Objects.equals(adjInfoId, that.adjInfoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gradeId, adjInfoId);
    }
}

