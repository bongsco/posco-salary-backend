package com.bongsco.api.adjust.annual.dto.response;

import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HighPerformanceTableResponse {
    // 직급, 직급별 등급, 직급등급별 가산 정보를 수정
    private Map<String, Map<String, RateInfo>> salaryIncrementByRank;
    private HpoSalaryInfo hpoSalaryInfo;
    private List<HighPerformanceEmployee> highPerformanceEmployees;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @ToString
    public static class RateInfo {
        private Double salaryIncrementRate;
        private Double bonusMultiplier;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @ToString
    public static class HpoSalaryInfo {
        private Double hpoSalaryIncrementRate;
        private Double hpoBonusMultiplier;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @ToString
    public static class HighPerformanceEmployee {
        private Long adjSubjectId;
        private Long employeeId;
        private String empNum;
        private String name;
        private String depName;
        private String gradeName;
        private String rankName;
        private Boolean isInHpo;
    }
}
