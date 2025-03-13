package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.bongsco.poscosalarybackend.adjust.domain.PaybandCriteria;
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
        private BigDecimal representativeVal;
        private BigDecimal upperLimitPrice;
        private BigDecimal lowerLimitPrice;
        private BigDecimal gradeBaseSalary;

        public static PaybandCriteriaResponse from(PaybandCriteria paybandCriteria, int numberOfEmpl,
            BigDecimal representativeVal) {
            return new PaybandCriteriaResponse(paybandCriteria.getGrade().getGradeName(), numberOfEmpl,
                representativeVal,
                paybandCriteria.getUpperLimitPrice(), paybandCriteria.getLowerLimitPrice(),
                paybandCriteria.getGrade().getGradeBaseSalary());
        }
    }
}
