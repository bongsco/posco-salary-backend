package com.bongsco.api.adjust.annual.dto.response;

import java.util.List;
import java.util.Map;

import com.bongsco.api.adjust.annual.dto.HpoPerDepartmentDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ResultChartResponse {
    private List<Map<String , Map<String, Double>>> salaryPerGrade; //[2021-2차:{p1:2000, p2: ...}]
    private List<Map<String, Object>> annualSalary; //[{년도: 2021,총연봉:200, 기준연봉: ...}]
    private List<HpoPerDepartmentDto> HpoPerDepartment; //[{팀: 개발팀, 인원: 3}]

    public static ResultChartResponse from() {
        return new ResultChartResponse();
    }

}
