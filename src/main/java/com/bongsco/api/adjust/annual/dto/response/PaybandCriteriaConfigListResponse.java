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
public class PaybandCriteriaConfigListResponse {
    private List<PaybandCriteriaConfig> paybandCriteriaConfigs;

    public static PaybandCriteriaConfigListResponse from(List<PaybandCriteria> paybandCriteriaList) {
        return new PaybandCriteriaConfigListResponse(
            paybandCriteriaList.stream().map(PaybandCriteriaConfig::from).toList());
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class PaybandCriteriaConfig {
        private Long id;
        private String grade;
        private Double upperBound;
        private Double lowerBound;

        public static PaybandCriteriaConfig from(PaybandCriteria paybandCriteria) {
            return new PaybandCriteriaConfig(paybandCriteria.getId(), paybandCriteria.getAdjustGrade().getGrade().getName(),
                paybandCriteria.getUpperBound(), paybandCriteria.getLowerBound());
        }
    }
}
