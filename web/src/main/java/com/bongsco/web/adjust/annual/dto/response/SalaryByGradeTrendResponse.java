package com.bongsco.web.adjust.annual.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalaryByGradeTrendResponse {
    private int year;
    private String grade;
    private long totalSalary;
}
