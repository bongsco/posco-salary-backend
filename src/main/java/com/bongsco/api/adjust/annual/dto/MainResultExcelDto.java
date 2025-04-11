package com.bongsco.api.adjust.annual.dto;

import com.bongsco.api.adjust.common.entity.PaybandAppliedType;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class MainResultExcelDto {
    private String empNum;
    private String name;
    private String gradeName;
    private String positionName;
    private String depName;
    private String rankCode;
    private Double stdSalaryIncrementRate;
    private Double finalStdSalary;
    private Double stdSalary;
    private Double hpoBonus;
    private Boolean isInHpo;
    private Long empId;
    private Long adjustSubjectId;
    private Long gradeId;
    private Long rankId;
    private Long adjustGradeId;
    private Double bonusMultiplier;
    private Double salaryIncrementRate;
    private PaybandAppliedType isPaybandApplied;
}

