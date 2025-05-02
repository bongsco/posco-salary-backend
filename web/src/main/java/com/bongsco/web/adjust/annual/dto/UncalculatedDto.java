package com.bongsco.web.adjust.annual.dto;

import com.bongsco.web.adjust.common.entity.PaybandAppliedType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UncalculatedDto {
    private String gradeName;
    private Double stdSalary;
    private Double hpoBonus;
    private PaybandAppliedType paybandAppliedType;
    private Double upperBoundMemo;
    private Double lowerBoundMemo;
}
