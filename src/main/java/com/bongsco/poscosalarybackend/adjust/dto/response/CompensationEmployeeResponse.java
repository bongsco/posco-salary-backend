package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class CompensationEmployeeResponse {
    private final Long adjSubjectId;
    private final String empNum;
    private final String name;
    private final String depName;
    private final String gradeName;
    private final String rankName;
    private final Boolean inHighPerformGroup;
    private final BigDecimal evalAnnualSalaryIncrement;
    private final BigDecimal evalPerformProvideRate;
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
