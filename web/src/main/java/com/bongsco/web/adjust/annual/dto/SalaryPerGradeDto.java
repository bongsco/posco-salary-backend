package com.bongsco.web.adjust.annual.dto;

import com.bongsco.web.adjust.common.domain.AdjustType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalaryPerGradeDto {
    private String gradeName;
    private Double totalStdSalary;
    private Double totalHpoBonus;
}
