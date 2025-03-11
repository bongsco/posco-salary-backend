package com.bongsco.poscosalarybackend.adjust.dto.response;

import java.util.List;

import lombok.Getter;

@Getter
public class MainAdjPaybandBothSubjectsResponse {
    private final List<MainAdjPaybandSubjectsResponse> upperAdjSubjects;
    private final List<MainAdjPaybandSubjectsResponse> lowerAdjSubjects;

    private MainAdjPaybandBothSubjectsResponse(List<MainAdjPaybandSubjectsResponse> upperAdjSubjects,
        List<MainAdjPaybandSubjectsResponse> lowerAdjSubjects) {
        this.upperAdjSubjects = upperAdjSubjects;
        this.lowerAdjSubjects = lowerAdjSubjects;
    }

    public static MainAdjPaybandBothSubjectsResponse of(List<MainAdjPaybandSubjectsResponse> upperAdjSubjects,
        List<MainAdjPaybandSubjectsResponse> lowerAdjSubjects) {
        return new MainAdjPaybandBothSubjectsResponse(upperAdjSubjects, lowerAdjSubjects);
    }
}
