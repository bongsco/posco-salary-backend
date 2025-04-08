package com.bongsco.api.adjust.annual.dto.response;

import java.util.List;

import com.bongsco.api.adjust.annual.entity.PaybandCriteria;

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
            return new PaybandCriteriaResponse(paybandCriteria.getGrade().getName(), numberOfEmpl,
                representativeVal,
                paybandCriteria.getUpperBound(), paybandCriteria.getLowerBound(),
                paybandCriteria.getGrade().getBaseSalary());
        }
    }
}
