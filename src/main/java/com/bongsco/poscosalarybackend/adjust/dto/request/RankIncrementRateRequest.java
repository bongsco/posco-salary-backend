package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.util.Map;

import jakarta.validation.Valid;
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
    @Valid
    @NotNull(message = "rankData가 null 일 수 없습니다.")
    private Map<Long, Map<Long, RankIncrementRateDetail>> rankData;

    @NotNull(message = "evalDiffBonusPromoted가 null 일 수 없습니다.")
    private Double evalDiffBonusPromoted;

    @NotNull(message = "evalDiffIncrementPromoted가 null 일 수 없습니다.")
    private Double evalDiffIncrementPromoted;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class RankIncrementRateDetail {
        @NotNull(message = "evalDiffBonus가 null 일 수 없습니다.")
        private Double evalDiffBonus;
        @NotNull(message = "evalDiffIncrement가 null 일 수 없습니다.")
        private Double evalDiffIncrement;
    }
}




