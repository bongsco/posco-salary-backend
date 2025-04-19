package com.bongsco.mobile.dto.response;

import java.util.Optional;

import com.bongsco.mobile.repository.reflection.ChartProjection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChartResponse {
    private Integer year;
    private Integer orderNumber;
    private Long stdSalary;       // Double → Long
    private Long hpoBonus;
    private Double bonusPrice;
    private Double salaryIncrementRate;
    private Double bonusMultiplier;


    public static ChartResponse of(ChartProjection projection) {
        Double salaryIncrementRate = Optional.ofNullable(projection.getSalaryIncrementRate()).orElse(0.0);
        Double bonusMultiplier = Optional.ofNullable(projection.getBonusMultiplier()).orElse(0.0);

        if (projection.getInHpo()!=null && projection.getInHpo()) {
            salaryIncrementRate = ((1 +salaryIncrementRate / 100) * (1 + Optional.ofNullable(projection.getHpoSalaryIncrementByRank()).orElse(0.0) / 100) - 1) * 100;
            bonusMultiplier += Optional.ofNullable(projection.getHpoBonusMultiplier()).orElse(0.0);
        }
        return new ChartResponse(
            projection.getYear(),
            projection.getOrderNumber(),
            Optional.ofNullable(projection.getStdSalary()).map(Double::longValue).orElse(null),
            Optional.ofNullable(projection.getHpoBonus()).map(Double::longValue).orElse(null),
            projection.getBonusPrice(), salaryIncrementRate, bonusMultiplier);
    }
}
