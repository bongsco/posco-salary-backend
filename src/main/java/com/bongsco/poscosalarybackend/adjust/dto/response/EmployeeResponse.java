package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.time.LocalDate;

import com.bongsco.poscosalarybackend.adjust.domain.AdjSubject;

import lombok.Getter;

@Getter
public class EmployeeResponse {
    private final long employeeId;
    private final String empNum;
    private final String name;
    private final LocalDate hireDate;
    private final String rankName;
    private final boolean subjectUse;

    private EmployeeResponse(long employeeId, String empNum, String name, LocalDate hireDate, String rankName,
        boolean subjectUse) {
        this.employeeId = employeeId;
        this.empNum = empNum;
        this.name = name;
        this.hireDate = hireDate;
        this.rankName = rankName;
        this.subjectUse = subjectUse;
    }

    public static EmployeeResponse of(long employeeId, String empNum, String name, LocalDate hireDate,
        String rankName, boolean subjectUse) {
        return new EmployeeResponse(employeeId, empNum, name, hireDate, rankName, subjectUse);
    }

    public static EmployeeResponse from(AdjSubject adjSubject) {
        return EmployeeResponse.of(adjSubject.getEmployee().getId(), adjSubject.getEmployee().getEmpNum(),
            adjSubject.getEmployee().getName(), adjSubject.getEmployee().getHireDate(),
            adjSubject.getEmployee().getRank().getRankCode(),
            adjSubject.getSubjectUse());
    }
}
