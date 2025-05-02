package com.bongsco.mobile.dto.response;

import java.time.LocalDate;
import java.util.Optional;

import com.bongsco.mobile.repository.reflection.AdjustDetailProjection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdjustDetailResponse {
    private Integer year;
    private Integer orderNumber;
    private String adjustType;
    private String author;
    private LocalDate baseDate;
    private LocalDate exceptionStartDate;
    private LocalDate exceptionEndDate;
    private String name;
    private String gradeName;
    private String departmentName;
    private String positionName;
    private LocalDate hireDate;
    private String employmentTypeName;
    private String rankCode;
    private Double salaryIncrementRate;
    private Double bonusMultiplier;
    private Boolean isInHpo;
    private Double hpoSalaryIncrementByRank;
    private Double hpoBonusMultiplier;
    private Double finalSalaryIncrementRate;
    private Double finalBonusMultiplier;
    private Double beforeStdSalary;
    private Double stdSalary;
    private Double hpoBonus;
    private String isPaybandApplied;
    private Double contractSalary;

    public static AdjustDetailResponse of(AdjustDetailProjection projection, Double beforeStdSalary) {
        Double finalSalaryIncrementRate = Optional.ofNullable(projection.getSalaryIncrementRate()).orElse(0.0);
        Double finalBonusMultiplier = Optional.ofNullable(projection.getBonusMultiplier()).orElse(0.0);

        if (projection.getIsInHpo() != null && projection.getIsInHpo()) {
            finalSalaryIncrementRate = ((1 + finalSalaryIncrementRate / 100) * (1
                + Optional.ofNullable(projection.getHpoSalaryIncrementByRank()).orElse(0.0) / 100) - 1) * 100;
            finalBonusMultiplier += Optional.ofNullable(projection.getHpoBonusMultiplier()).orElse(0.0);
        }
        return new AdjustDetailResponse(
            projection.getYear(),
            projection.getOrderNumber(),
            projection.getAdjustType().getDisplayName(),
            projection.getAuthor(),
            projection.getBaseDate(),
            projection.getExceptionStartDate(),
            projection.getExceptionEndDate(),
            projection.getName(),
            projection.getGradeName(),
            projection.getDepartmentName(),
            projection.getPositionName(),
            projection.getHireDate(),
            projection.getEmploymentTypeName(),
            projection.getRankCode(),
            projection.getSalaryIncrementRate(),
            projection.getBonusMultiplier(),
            projection.getIsInHpo(),
            projection.getHpoSalaryIncrementByRank() == null ? 0.0 : projection.getHpoSalaryIncrementByRank(),
            projection.getHpoBonusMultiplier() == null ? 0.0 : projection.getHpoBonusMultiplier(),
            finalSalaryIncrementRate,
            finalBonusMultiplier,
            beforeStdSalary,
            projection.getStdSalary(),
            projection.getHpoBonus(),
            projection.getIsPaybandApplied()==null ? "미적용" : projection.getIsPaybandApplied().getDisplayName(),
            Optional.ofNullable(projection.getStdSalary()).orElse(0.0) + Optional.ofNullable(projection.getHpoBonus())
                .orElse(0.0)
        );
    }
}
