package com.bongsco.api.adjust.annual.dto.request;

import java.time.LocalDate;

import com.bongsco.api.global.domain.AdjustType;

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
public class AdjustPostRequest {
    @NotNull(message = "조정 유형(type)은 필수 입력값입니다.")
    private AdjustType type;

    @NotNull(message = "적용 시작 일자(startDate)는 필수 입력값입니다.")
    private LocalDate startDate;

    @NotNull(message = "적용 만료 일자(endDate)는 필수 입력값입니다.")
    private LocalDate endDate;
}
