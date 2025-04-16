package com.bongsco.web.adjust.annual.dto.response;

import java.time.LocalDate;

import com.bongsco.web.adjust.common.entity.AdjustSubject;
import com.bongsco.web.employee.entity.Employee;
import com.bongsco.web.employee.entity.Rank;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EmployeeResponse {
    private Long employeeId;
    private String empNum;
    private String name;
    private LocalDate hireDate;
    private String rankName;
    private Boolean subjectUse;

    public static EmployeeResponse from(AdjustSubject adjustSubject) {
        Employee emp = adjustSubject.getEmployee();
        Rank rank = adjustSubject.getRank();

        return new EmployeeResponse(
            emp.getId(),
            emp.getEmpNum(),
            emp.getName(),
            emp.getHireDate(),
            rank.getCode(),
            adjustSubject.getIsSubject()
        );
    }
}
