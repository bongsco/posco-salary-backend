package com.bongsco.poscosalarybackend.adjust.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AdjSubjectSalaryDto {
    private Long adjSubjectId;
    private Long employeeId;
    private BigDecimal stdSalary;
    private Boolean paybandUse;
}