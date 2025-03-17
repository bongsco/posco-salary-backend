package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SubjectCriteriaRequest {

    @NotNull(message = "baseDate는 null 일 수 없습니다.")
    private LocalDate baseDate;

    @NotNull(message = "exceptionStartDate는 null 일 수 없습니다.")
    private LocalDate exceptionStartDate;

    @NotNull(message = "exceptionEndDate는 null 일 수 없습니다.")
    private LocalDate exceptionEndDate;

    @NotNull(message = "gradeIds는 null 일 수 없습니다.")
    private List<Long> gradeIds;

    @NotNull(message = "paymentCriteriaIds는 null 일 수 없습니다.")
    private List<Long> paymentCriteriaIds;
}
