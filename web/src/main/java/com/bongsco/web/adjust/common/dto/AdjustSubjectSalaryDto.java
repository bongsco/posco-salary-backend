package com.bongsco.web.adjust.common.dto;

import com.bongsco.web.adjust.common.entity.PaybandAppliedType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdjustSubjectSalaryDto {
    private Long adjustSubjectId;
    private Long employeeId;
    private Double stdSalary;
    private Double finalStdSalary;
    private PaybandAppliedType isPaybandApplied;
    private Long gradeId;
    private String empNum;
    private String name;
    private String depName;
    private String gradeName;
    private String positionName;
    private String rankCode;
    private Double boundPercent;
    private Double baseSalary;
}

