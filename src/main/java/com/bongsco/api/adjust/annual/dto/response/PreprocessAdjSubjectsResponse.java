package com.bongsco.api.adjust.annual.dto.response;

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
public class PreprocessAdjSubjectsResponse {
    private List<CompensationEmployeeResponse> adjSubjects;
}
