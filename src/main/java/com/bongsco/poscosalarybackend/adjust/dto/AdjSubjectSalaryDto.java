package com.bongsco.poscosalarybackend.adjust.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdjSubjectSalaryDto {
    private final Long adjSubjectId;
    private final String empNum;
    private final String name;
    private final String depName;
    private final String gradeName;
    private final Long gradeId;
    private final String positionName;
    private final String rankName;
    private final BigDecimal stdSalary;
    private final Boolean paybandUse;
    private BigDecimal limitPrice;

    public AdjSubjectSalaryDto(Long adjSubjectId, String empNum, String name, String depName, String gradeName,
        Long gradeId, String positionName, String rankName, BigDecimal stdSalary, Boolean paybandUse) {
        this.adjSubjectId = adjSubjectId;
        this.empNum = empNum;
        this.name = name;
        this.depName = depName;
        this.gradeName = gradeName;
        this.gradeId = gradeId;
        this.positionName = positionName;
        this.rankName = rankName;
        this.stdSalary = stdSalary;
        this.paybandUse = paybandUse;
    }

    public AdjSubjectSalaryDto(Long adjSubjectId, String empNum, String name, String depName, String gradeName,
        Long gradeId, String positionName, String rankName, BigDecimal stdSalary, Boolean paybandUse,
        BigDecimal limitPrice) {
        this(adjSubjectId, empNum, name, depName, gradeName, gradeId, positionName, rankName, stdSalary, paybandUse);
        this.limitPrice = limitPrice;
    }
}