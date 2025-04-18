package com.bongsco.web.adjust.annual.dto.request;

import java.util.List;

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
public class PaybandCriteriaModifyRequest {

    @Valid
    @NotNull(message = "빈 map입니다.")
    private List<PaybandCriteriaModifyDetail> paybandCriteriaModifyDetailList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PaybandCriteriaModifyDetail {
        @NotNull(message = "id가 null 일 수 없습니다.")
        private Long id;
        @NotNull(message = "상한값이 null 일 수 없습니다.")
        private Double upperBound;
        @NotNull(message = "하한값이 null 일 수 없습니다.")
        private Double lowerBound;
    }
}

