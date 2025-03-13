package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.math.BigDecimal;
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
public class PaybandCriteriaRequest {

    @Valid
    @NotNull(message = "빈 map입니다.")
    private Map<Long, PaybandCriteriaDetail> gradeData;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PaybandCriteriaDetail {
        @NotNull(message = "상한값이 null 일 수 없습니다.")
        private BigDecimal upperLimitPrice;
        @NotNull(message = "하한값이 null 일 수 없습니다.")
        private BigDecimal lowerLimitPrice;
    }
}

