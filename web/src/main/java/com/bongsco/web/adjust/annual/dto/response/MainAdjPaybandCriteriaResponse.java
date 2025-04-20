package com.bongsco.web.adjust.annual.dto.response;

import java.util.List;


import com.bongsco.web.adjust.annual.entity.PaybandCriteria;
import com.bongsco.web.employee.entity.Grade;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MainAdjPaybandCriteriaResponse {
    private List<PaybandCriteriaResponse> PaybandCriteriaResponse;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class PaybandCriteriaResponse {
        private String gradeName;
        private int numberOfEmpl;
        private Double representativeVal;
        private Double upperLimitPrice;
        private Double lowerLimitPrice;
        private Double gradeBaseSalary;

        public static PaybandCriteriaResponse from(PaybandCriteria paybandCriteria, int numberOfEmpl,
            Double representativeVal) {
            Grade pcGrade= paybandCriteria.getAdjustGrade().getGrade();
            return new PaybandCriteriaResponse(pcGrade.getName(), numberOfEmpl,
                representativeVal,
                paybandCriteria.getUpperBound(), paybandCriteria.getLowerBound(),
                pcGrade.getBaseSalary());
        }
    }
}
