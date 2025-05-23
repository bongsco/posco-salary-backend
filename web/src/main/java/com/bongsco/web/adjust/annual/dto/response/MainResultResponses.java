package com.bongsco.web.adjust.annual.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MainResultResponses {
    private List<MainResultResponse> results;
    private Integer totalPages;
    private Integer pageNumber;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class MainResultResponse {
        private String empNum;
        private String name;
        private String gradeName;
        private String positionName;
        private String depName;
        private String rankCode;
        private Double salaryIncrementRate;
        private Double bonusMultiplier;
        private Double stdSalaryIncrementRate;
        private String payband;
        private Double salaryBefore;
        private Double stdSalary;
        private Double totalSalaryBefore;
        private Double totalSalary;
    }
}
