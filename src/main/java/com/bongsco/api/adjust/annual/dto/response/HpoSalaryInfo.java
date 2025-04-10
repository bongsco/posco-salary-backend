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
public class HpoSalaryInfo {
    private Double hpoSalaryIncrementRate;
    private Double hpoBonusMultiplier;
}
