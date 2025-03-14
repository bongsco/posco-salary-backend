package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.util.List;

import com.bongsco.poscosalarybackend.adjust.dto.AdjSubjectSalaryDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MainAdjPaybandBothSubjectsResponse {
    private List<MainAdjPaybandSubjectsResponse> upperAdjSubjects;
    private List<MainAdjPaybandSubjectsResponse> lowerAdjSubjects;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class MainAdjPaybandSubjectsResponse {
        private Long adjSubjectId;
        private String empNum;
        private String name;
        private String depName;
        private String gradeName;
        private String positionName;
        private String rankName;
        private Double stdSalary;
        private Double limitPrice;
        private Boolean paybandUse;

        public static MainAdjPaybandSubjectsResponse from(
            AdjSubjectSalaryDto adjSubjectSalaryDto
        ) {
            return new MainAdjPaybandSubjectsResponse(
                adjSubjectSalaryDto.getAdjSubjectId(),
                adjSubjectSalaryDto.getEmpNum(), adjSubjectSalaryDto.getName(), adjSubjectSalaryDto.getDepName(),
                adjSubjectSalaryDto.getGradeName(), adjSubjectSalaryDto.getPositionName(),
                adjSubjectSalaryDto.getRankName(), adjSubjectSalaryDto.getStdSalary(),
                adjSubjectSalaryDto.getLimitPrice(),
                adjSubjectSalaryDto.getPaybandUse());
        }
    }
}
