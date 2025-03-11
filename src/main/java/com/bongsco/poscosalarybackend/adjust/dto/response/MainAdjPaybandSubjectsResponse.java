package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class MainAdjPaybandSubjectsResponse {
    private final Long employeeId;
    private final String empNum;
    private final String name;
    private final LocalDate hireDate;
    private final String rankName;
    private final Boolean subjectUse;

    public MainAdjPaybandSubjectsResponse(Long employeeId, String empNum, String name, LocalDate hireDate,
        String rankName, Boolean subjectUse) {
        this.employeeId = employeeId;
        this.empNum = empNum;
        this.name = name;
        this.hireDate = hireDate;
        this.rankName = rankName;
        this.subjectUse = subjectUse;
    }
}
