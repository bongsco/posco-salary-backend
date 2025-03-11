package com.bongsco.poscosalarybackend.adjust.dto;

import java.math.BigDecimal;

import com.bongsco.poscosalarybackend.adjust.domain.PaybandCriteria;

import lombok.Getter;

@Getter
public class MainAdjPaybandCriteriaResponse {
    private final String gradeName;
    private final int numberOfEmpl;
    private final BigDecimal representativeVal;
    private final BigDecimal upperLimitPrice;
    private final BigDecimal lowerLimitPrice;
    private final BigDecimal gradeBaseSalary;

    private MainAdjPaybandCriteriaResponse(String gradeName, int numberOfEmpl, BigDecimal representativeVal,
        BigDecimal upperLimitPrice, BigDecimal lowerLimitPrice, BigDecimal gradeBaseSalary) {
        this.gradeName = gradeName;
        this.numberOfEmpl = numberOfEmpl;
        this.representativeVal = representativeVal;
        this.upperLimitPrice = upperLimitPrice;
        this.lowerLimitPrice = lowerLimitPrice;
        this.gradeBaseSalary = gradeBaseSalary;
    }

    public static MainAdjPaybandCriteriaResponse of(String gradeName, int numberOfEmpl, BigDecimal representativeVal,
        BigDecimal upperLimitPrice, BigDecimal lowerLimitPrice, BigDecimal gradeBaseSalary) {
        return new MainAdjPaybandCriteriaResponse(gradeName, numberOfEmpl, representativeVal, upperLimitPrice,
            lowerLimitPrice, gradeBaseSalary);
    }

    public static MainAdjPaybandCriteriaResponse from(PaybandCriteria paybandCriteria, int numberOfEmpl,
        BigDecimal representativeVal) {
        return MainAdjPaybandCriteriaResponse.of(paybandCriteria.getGrade().getGradeName(), numberOfEmpl,
            BigDecimal.valueOf(0),
            paybandCriteria.getUpperLimitPrice(), paybandCriteria.getLowerLimitPrice(),
            paybandCriteria.getGrade().getGradeBaseSalary());
    }
}
