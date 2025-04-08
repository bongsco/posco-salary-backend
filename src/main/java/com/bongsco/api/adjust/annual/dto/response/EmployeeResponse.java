package com.bongsco.api.adjust.annual.dto.response;

import java.time.LocalDate;

import com.bongsco.api.adjust.common.entity.AdjustSubject;

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
        return new EmployeeResponse(adjustSubject.getEmployee().getId(), adjustSubject.getEmployee().getEmpNum(),
            adjustSubject.getEmployee().getName(), adjustSubject.getEmployee().getHireDate(),
            adjustSubject.getRank().getCode(),
            adjustSubject.getIsSubject());
    }
}
