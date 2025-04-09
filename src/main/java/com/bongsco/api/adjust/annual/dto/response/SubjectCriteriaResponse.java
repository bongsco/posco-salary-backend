package com.bongsco.api.adjust.annual.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SubjectCriteriaResponse {

    private LocalDate baseDate;
    private LocalDate expStartDate;
    private LocalDate expEndDate;

    private List<SelectableItemDto> grades;
    private List<SelectableItemDto> payments;
}

