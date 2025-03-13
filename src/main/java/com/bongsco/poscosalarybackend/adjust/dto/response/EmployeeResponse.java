package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.time.LocalDate;

import com.bongsco.poscosalarybackend.adjust.domain.AdjSubject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmployeeResponse {
    private long employeeId;
    private String empNum;
    private String name;
    private LocalDate hireDate;
    private String rankName;
    private boolean subjectUse;
    
    public static EmployeeResponse from(AdjSubject adjSubject) {

        return new EmployeeResponse(adjSubject.getEmployee().getId(), adjSubject.getEmployee().getEmpNum(),
            adjSubject.getEmployee().getName(), adjSubject.getEmployee().getHireDate(),
            adjSubject.getEmployee().getRank().getRankCode(),
            adjSubject.getSubjectUse());
    }
}
