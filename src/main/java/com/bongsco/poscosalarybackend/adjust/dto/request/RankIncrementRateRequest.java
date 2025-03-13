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
public class RankIncrementRateRequest {
    @NotNull
    private Map<Long, Map<Long, RankIncrementRateDetail>> rankData;

    @NotNull
    private BigDecimal evalDiffBonusPromoted;

    @NotNull
    private BigDecimal evalDiffIncrementPromoted;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class RankIncrementRateDetail {
        private BigDecimal evalDiffBonus;
        private BigDecimal evalDiffIncrement;
    }
}




