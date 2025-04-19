package com.bongsco.web.adjust.annual.dto.response;

import java.util.List;
import java.util.Map;

import com.bongsco.web.adjust.annual.dto.HpoPerDepartmentDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ResultChartResponse {
    private List<SalaryPerGrade> salaryPerGrades; //[2021-2차:{p1:2000, p2: ...}]
    private List<AnnualSalary> annualSalaries; //[{조정차수: 2021-2차,총연봉:200, 기준연봉: ...}]
    private List<HpoPerDepartmentDto> hpoPerDepartments; //[{팀: 개발팀, 인원: 3}]

    public static ResultChartResponse from(List<SalaryPerGrade> salaryPerGrades, List<AnnualSalary> annualSalarys, List<HpoPerDepartmentDto> hpoPerDepartments) {
        return new ResultChartResponse(salaryPerGrades, annualSalarys, hpoPerDepartments);
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class SalaryPerGrade {
        private String adjustName;
        private Map<String, Double> gradeSalaryMap;

        public static SalaryPerGrade from(String adjustName, Map<String, Double> gradeSalaryMap) {return new SalaryPerGrade(adjustName, gradeSalaryMap);}
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class AnnualSalary {
        private String adjustName;
        private Double stdSalary;
        private Double hpoBonus;
        private Double totalSalary;

        public static AnnualSalary from(String adjustName, Double stdSalary, Double hpoBonus, Double totalSalary) {return new AnnualSalary(adjustName, stdSalary, hpoBonus, totalSalary);}
    }

}
