package com.bongsco.api.adjust.annual.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CompensationEmployeeResponse {
    private Long adjSubjectId;
    private String empNum;
    private String name;
    private String depName;
    private String gradeName;
    private String rankName;
    private Boolean inHighPerformGroup;
    private Double evalAnnualSalaryIncrement;
    private Double evalPerformProvideRate;
    private Double evalDiffIncrement;
    private Double evalDiffBonus;

    public CompensationEmployeeResponse(Long adjSubjectId, String empNum, String name, String depName, String gradeName,
        String rankName, Boolean inHighPerformGroup, Double evalAnnualSalaryIncrement,
        Double evalPerformProvideRate) {
        this.adjSubjectId = adjSubjectId;
        this.empNum = empNum;
        this.name = name;
        this.depName = depName;
        this.gradeName = gradeName;
        this.rankName = rankName;
        this.inHighPerformGroup = inHighPerformGroup;
        this.evalAnnualSalaryIncrement = evalAnnualSalaryIncrement;
        this.evalPerformProvideRate = evalPerformProvideRate;
    }

    public void setEvalDiffIncrement(Double evalDiffIncrement) {
        this.evalDiffIncrement = evalDiffIncrement;
    }

    public void setEvalDiffBonus(Double evalDiffBonus) {
        this.evalDiffBonus = evalDiffBonus;
    }
}
