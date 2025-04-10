package com.bongsco.api.adjust.annual.dto.response;

import java.util.List;

import com.bongsco.api.adjust.common.dto.AdjustSubjectSalaryDto;
import com.bongsco.api.adjust.common.entity.PaybandAppliedType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaybandSubjectResponse {
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
        private PaybandAppliedType isPaybandApplied;

        public static MainAdjustPaybandSubjectsResponse from(AdjustSubjectSalaryDto dto) {
            double limitPrice = dto.getBaseSalary() * (dto.getBoundPercent() / 100.0);

            return new MainAdjustPaybandSubjectsResponse(
                dto.getAdjustSubjectId(),
                dto.getEmpNum(),
                dto.getName(),
                dto.getDepName(),
                dto.getGradeName(),
                dto.getPositionName(),
                dto.getRankCode(),
                dto.getStdSalary(),
                limitPrice,
                dto.getIsPaybandApplied()
            );
        }
    }
}
