package com.bongsco.mobile.dto.response;

import com.bongsco.mobile.repository.reflection.BarChartProjection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BarChartResponse {
    private Integer year;
    private Integer orderNumber;
    private Double stdSalary;
    private Double hpoBonus;
    private Double bonusPrice;

    public BarChartResponse(int year, int orderNumber, double stdSalary, double hpoBonus, double bonusPrice) {
        this.year = year;
        this.orderNumber = orderNumber;
        this.stdSalary = stdSalary;
        this.hpoBonus = hpoBonus;
        this.bonusPrice = bonusPrice;
    }

}
