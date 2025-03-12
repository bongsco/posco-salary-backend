package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.math.BigDecimal;

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
    private BigDecimal evalAnnualSalaryIncrement;
    private BigDecimal evalPerformProvideRate;
    private BigDecimal evalDiffIncrement;
    private BigDecimal evalDiffBonus;

    public CompensationEmployeeResponse(Long adjSubjectId, String empNum, String name, String depName, String gradeName,
        String rankName, Boolean inHighPerformGroup, BigDecimal evalAnnualSalaryIncrement,
        BigDecimal evalPerformProvideRate) {
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

    public void setEvalDiffIncrement(BigDecimal evalDiffIncrement) {
        this.evalDiffIncrement = evalDiffIncrement;
    }

    public void setEvalDiffBonus(BigDecimal evalDiffBonus) {
        this.evalDiffBonus = evalDiffBonus;
    }
}
