package com.bongsco.api.adjust.annual.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeadCountTrendResponse {
    private String adjustCycle;     // 예: "22
    private int headcount;          // 총 인원
    private Double changeRate;      // 전년 동월 대비 증감률(%)
}
