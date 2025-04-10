package com.bongsco.api.adjust.annual.dto;

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
    private Long empId;
    private Double baseSalary;
    private Double salaryIncrementRate;
    private Double bonusMultiplier;
    private Long adjustSubjectId;
    private Boolean isInHpo;
}
