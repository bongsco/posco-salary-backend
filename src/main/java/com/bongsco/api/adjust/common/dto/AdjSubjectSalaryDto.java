package com.bongsco.api.adjust.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdjSubjectSalaryDto {
    private Long adjSubjectId;
    private Long employeeId;
    private Double stdSalary;
    private Double finalStdSalary;
    private Boolean paybandUse;
    private Long gradeId;
    private String empNum;
    private String name;
    private String depName;
    private String gradeName;
    private String positionName;
    private String rankName;
    private Double limitPrice;
}
