package com.bongsco.api.adjust.annual.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RateInfo {
    private String gradeName;
    private String rankCode;
    private Double salaryIncrementRate;
    private Double bonusMultiplier;
}
