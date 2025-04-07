package com.bongsco.api.adjust.annual.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdjResultResponse {
    private List<AdjResult> adjResults;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class AdjResult {
        private String empNum;
        private String name;
        private LocalDate birth;
        private LocalDate hireDate;
        private String paymentName;
        private String depName;
        private String positionName;
        private String gradeName;
        private String positionArea;
        private Double evalAnnualSalaryIncrement;
        private Double evalPerformProvideRate;
        private Double upperLimitPrice;
        private Double lowerLimitPrice;
        private Double beforeUsingLowerPayband;
        private Integer beforeYear;
        private Integer beforeOrder;
        private Double stdSalary;
        private Double beforePerformAddPayment;
        private Double beforeContractTotalSalary;
        private Double representativeVal;
        private Integer year;
        private Integer order;
        private Double finalStdSalaryIncrementRate;
        private Double finalStdSalary;
        private Double finalPerformAddPayment;
        private Double finalContractTotalSalary;
    }
}
