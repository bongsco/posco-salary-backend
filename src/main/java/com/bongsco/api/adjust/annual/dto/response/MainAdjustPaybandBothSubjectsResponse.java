package com.bongsco.api.adjust.annual.dto.response;

import java.util.List;

import com.bongsco.api.adjust.common.dto.AdjustSubjectSalaryDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MainAdjustPaybandBothSubjectsResponse {
    private List<MainAdjustPaybandSubjectsResponse> upperAdjustSubjects;
    private List<MainAdjustPaybandSubjectsResponse> lowerAdjustSubjects;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class MainAdjustPaybandSubjectsResponse {
        private Long adjustSubjectId;
        private String empNum;
        private String name;
        private String depName;
        private String gradeName;
        private String positionName;
        private String rankCode;
        private Double stdSalary;
        private Double limitPrice;
        private Boolean isPaybandApplied;

        public static MainAdjustPaybandSubjectsResponse from(
            AdjustSubjectSalaryDto adjustSubjectSalaryDto
        ) {
            return new MainAdjustPaybandSubjectsResponse(
                adjustSubjectSalaryDto.getAdjustSubjectId(),
                adjustSubjectSalaryDto.getEmpNum(), adjustSubjectSalaryDto.getName(),
                adjustSubjectSalaryDto.getDepName(),
                adjustSubjectSalaryDto.getGradeName(), adjustSubjectSalaryDto.getPositionName(),
                adjustSubjectSalaryDto.getRankCode(), adjustSubjectSalaryDto.getStdSalary(),
                adjustSubjectSalaryDto.getLimitPrice(),
                adjustSubjectSalaryDto.getIsPaybandApplied());
        }
    }
}
