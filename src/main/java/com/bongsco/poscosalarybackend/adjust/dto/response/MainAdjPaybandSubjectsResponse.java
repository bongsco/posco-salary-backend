package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.math.BigDecimal;

import com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto;

import lombok.Getter;

@Getter
public class MainAdjPaybandSubjectsResponse {
    private final Long adjSubjectId;
    private final String empNum;
    private final String name;
    private final String depName;
    private final String gradeName;
    private final String positionName;
    private final String rankName;
    private final BigDecimal stdSalary;
    private final BigDecimal limitPrice;
    private final Boolean paybandUse;

    public MainAdjPaybandSubjectsResponse(Long adjSubjectId, String empNum, String name, String depName,
        String gradeName, String positionName, String rankName, BigDecimal stdSalary, BigDecimal limitPrice,
        Boolean paybandUse) {
        this.adjSubjectId = adjSubjectId;
        this.empNum = empNum;
        this.name = name;
        this.depName = depName;
        this.gradeName = gradeName;
        this.positionName = positionName;
        this.rankName = rankName;
        this.stdSalary = stdSalary;
        this.limitPrice = limitPrice;
        this.paybandUse = paybandUse;
    }

    public static MainAdjPaybandSubjectsResponse from(AdjSubjectSalaryDto adjSubjectSalaryDto) {
        return new MainAdjPaybandSubjectsResponse(adjSubjectSalaryDto.getAdjSubjectId(),
            adjSubjectSalaryDto.getEmpNum(), adjSubjectSalaryDto.getName(), adjSubjectSalaryDto.getDepName(),
            adjSubjectSalaryDto.getGradeName(), adjSubjectSalaryDto.getPositionName(),
            adjSubjectSalaryDto.getRankName(), adjSubjectSalaryDto.getStdSalary(), adjSubjectSalaryDto.getLimitPrice(),
            adjSubjectSalaryDto.getPaybandUse());
    }
}
