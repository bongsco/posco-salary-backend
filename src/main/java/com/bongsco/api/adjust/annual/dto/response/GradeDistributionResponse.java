package com.bongsco.api.adjust.annual.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GradeDistributionResponse {
    private String grade;
    private int count;
    private double percentage;
}
