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
public class HpoEmployeesResponse {
    // 직급, 직급별 등급, 직급등급별 가산 정보를 수정
    private List<RateInfo> salaryIncrementByRank;
    private HpoSalaryInfo hpoSalaryInfo;
    private List<HpoEmployee> highPerformanceEmployees;
}
