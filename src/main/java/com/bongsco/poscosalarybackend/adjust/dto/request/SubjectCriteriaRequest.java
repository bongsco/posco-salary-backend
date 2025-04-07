package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.time.LocalDate;
import java.util.Map;

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
    private LocalDate expStartDate;

    @NotNull(message = "exceptionEndDate는 null 일 수 없습니다.")
    private LocalDate expEndDate;

    @NotNull(message = "grades는 null 일 수 없습니다.")
    private Map<Long, Boolean> gradeSelections;      // ✅ 체크된 등급 ID만 보내기

    @NotNull(message = "payments는 null 일 수 없습니다.")
    private Map<Long, Boolean> paymentSelections;    // ✅ 체크된 직급 ID만 보내기
}
