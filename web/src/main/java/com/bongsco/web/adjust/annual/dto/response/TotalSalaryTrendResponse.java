package com.bongsco.web.adjust.annual.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TotalSalaryTrendResponse {
    private long averageSalary;
    private double increaseRate;
    private List<SalaryRangeResponse> salaryRanges;
}
