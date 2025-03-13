package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.math.BigDecimal;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaybandCriteriaRequest {

    @NotNull
    private Map<Long, PaybandCriteriaDetail> gradeData;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PaybandCriteriaDetail {
        private BigDecimal upperLimitPrice;
        private BigDecimal lowerLimitPrice;
    }
}

