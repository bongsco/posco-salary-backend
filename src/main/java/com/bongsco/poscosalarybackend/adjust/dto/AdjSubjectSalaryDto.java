package com.bongsco.poscosalarybackend.adjust.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AdjSubjectSalaryDto {
    private Long adjSubjectId;
    private String empNum;
    private String name;
    private String depName;
    private String gradeName;
    private Long gradeId;
    private String positionName;
    private String rankName;
    private BigDecimal stdSalary;
    private Boolean paybandUse;
    private BigDecimal limitPrice;
}