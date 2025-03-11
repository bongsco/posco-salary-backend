package com.bongsco.poscosalarybackend.adjust.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class SalaryId implements Serializable {

    private Long adjInfoId;
    private Long employeeId;

    public SalaryId() {
    }

    public SalaryId(Long adjInfoId, Long employeeId) {
        this.adjInfoId = adjInfoId;
        this.employeeId = employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SalaryId salaryId = (SalaryId)o;
        return Objects.equals(adjInfoId, salaryId.adjInfoId) &&
            Objects.equals(employeeId, salaryId.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adjInfoId, employeeId);
    }
}

