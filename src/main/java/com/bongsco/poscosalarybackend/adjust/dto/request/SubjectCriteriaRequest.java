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

    @NotNull
    private LocalDate baseDate;

    @NotNull
    private LocalDate exceptionStartDate;

    @NotNull
    private LocalDate exceptionEndDate;

    @NotNull
    private List<Long> gradeIds;

    @NotNull
    private List<Long> paymentCriteriaIds;
}
