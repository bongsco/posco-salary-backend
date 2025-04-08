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
public class MainResultDto {
    private String empNum;
    private String name;
    private String gradeName;
    private String positionName;
    private String depName;
    private String rankName;
    private Double salaryIncrementRate;
    private Double finalStdSalary;
    private Double hpoBonus;
    private Long empId;
    private Long adjustSubjectId;
}
