package com.bongsco.web.adjust.annual.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.web.adjust.annual.dto.response.EmploymentTypeDistributionResponse;
import com.bongsco.web.adjust.annual.dto.response.GradeDistributionResponse;
import com.bongsco.web.adjust.annual.dto.response.HeadCountTrendResponse;
import com.bongsco.web.adjust.annual.dto.response.SalaryByGradeTrendResponse;
import com.bongsco.web.adjust.annual.dto.response.SalaryRangeResponse;
import com.bongsco.web.adjust.annual.dto.response.TenureDistributionResponse;
import com.bongsco.web.adjust.annual.dto.response.TotalSalaryTrendResponse;
import com.bongsco.web.adjust.common.entity.Adjust;
import com.bongsco.web.adjust.common.repository.AdjustRepository;
import com.bongsco.web.adjust.common.repository.AdjustSubjectRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "MainChartAPI", description = "Main 차트 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class MainChartController {
    private final AdjustSubjectRepository adjustSubjectRepository;
    private final AdjustRepository adjustRepository;

    @Operation(summary = "총 인력현황", description = "총 인력 현황과 전년 대비 입사율")
    @GetMapping("headcountTrend")
    public ResponseEntity<List<HeadCountTrendResponse>> getHeadcountTrend() {
        List<Object[]> rawList = adjustSubjectRepository.getHeadcountTrendRaw();

        List<HeadCountTrendResponse> result = rawList.stream().map(row -> {
            String adjustCycle = (String)row[0];
            int headcount = row[1] == null ? 0 : ((Number)row[1]).intValue();
            int prev = row[2] == null ? 0 : ((Number)row[2]).intValue();

            Double changeRate = (prev == 0)
                ? 0.0
                : Math.round(((headcount - prev) * 100.0 / prev) * 10.0) / 10.0;

            return new HeadCountTrendResponse(adjustCycle, headcount, changeRate);
        }).toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/gradeDistribution")
    @Operation(summary = "직급별 인력분포", description = "가장 최근 ANNUAL 조정 차수의 직급별 인원 분포 및 비율")
    public ResponseEntity<List<GradeDistributionResponse>> getGradeDistribution() {
        Long latestAdjustId = adjustRepository.findLatestAnnualAdjustId();

        if (latestAdjustId == null) {
            return ResponseEntity.noContent().build();
        }

        List<Object[]> rows = adjustSubjectRepository.getGradeDistribution(latestAdjustId);

        List<GradeDistributionResponse> result = rows.stream()
            .map(row -> new GradeDistributionResponse(
                (String)row[0],
                ((Number)row[1]).intValue(),
                ((Number)row[2]).doubleValue()
            ))
            .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/employmentDistribution")
    @Operation(summary = "신분별 인력 분포", description = "가장 최근 ANNUAL 조정 차수의 신분별 인원 분포 및 비율")
    public ResponseEntity<List<EmploymentTypeDistributionResponse>> getEmploymentDistribution() {
        Long latestAdjustId = adjustRepository.findLatestAnnualAdjustId();

        if (latestAdjustId == null) {
            return ResponseEntity.noContent().build();
        }

        List<Object[]> rows = adjustSubjectRepository.getEmploymentTypeDistribution(latestAdjustId);

        List<EmploymentTypeDistributionResponse> result = rows.stream()
            .map(row -> new EmploymentTypeDistributionResponse(
                (String)row[0],
                ((Number)row[1]).intValue(),
                ((Number)row[2]).doubleValue()
            ))
            .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tenureDistribution")
    @Operation(summary = "근속연차별 인원 분포", description = "가장 최근 ANNUAL 차수 기준 직원들의 근속연차별 인원 수")
    public ResponseEntity<List<TenureDistributionResponse>> getTenureDistribution() {
        Long latestAdjustId = adjustRepository.findLatestAnnualAdjustId();

        if (latestAdjustId == null) {
            return ResponseEntity.noContent().build();
        }

        List<Object[]> rows = adjustSubjectRepository.getTenureDistribution(latestAdjustId);

        List<TenureDistributionResponse> result = rows.stream()
            .map(row -> new TenureDistributionResponse(
                ((Number)row[0]).intValue(),
                ((Number)row[1]).intValue()
            ))
            .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/totalSalaryTrend")
    @Operation(summary = "총 급여 상승률 + 구간별 분포", description = "가장 최근 ANNUAL 차수 기준 총 급여 합계, 전년 대비 상승률, 구간별 인원 수 반환")
    public ResponseEntity<TotalSalaryTrendResponse> getTotalSalaryTrend() {
        Long latestAdjustId = adjustRepository.findLatestAnnualAdjustId();
        if (latestAdjustId == null)
            return ResponseEntity.noContent().build();

        Adjust latestAdjust = adjustRepository.findById(latestAdjustId).orElseThrow();
        int currentYear = latestAdjust.getYear();
        int prevYear = currentYear - 1;

        Long currentTotal = adjustSubjectRepository.getCurrentTotalFinalSalary(latestAdjustId);
        Long prevTotal = adjustSubjectRepository.getPrevTotalFinalSalary(prevYear);

        if (currentTotal == null)
            currentTotal = 0L;
        if (prevTotal == null)
            prevTotal = 0L;

        double increaseRate = (prevTotal == 0) ? 0.0 :
            Math.round(((currentTotal - prevTotal) * 100.0 / prevTotal) * 10.0) / 10.0;

        // 평균 급여
        Double avgSalary = adjustSubjectRepository.getAverageFinalSalary(latestAdjustId);
        long averageSalary = avgSalary == null ? 0L : Math.round(avgSalary);

        // 급여 구간별 분포 조회
        List<Object[]> rangeRows = adjustSubjectRepository.getSalaryRangeDistribution(latestAdjustId);
        List<SalaryRangeResponse> salaryRanges = rangeRows.stream()
            .map(row -> new SalaryRangeResponse((String)row[0], ((Number)row[1]).intValue()))
            .toList();

        return ResponseEntity.ok(
            TotalSalaryTrendResponse.builder()
                .averageSalary(averageSalary)
                .increaseRate(increaseRate)
                .salaryRanges(salaryRanges)
                .build()
        );
    }

    @GetMapping("/salaryGradeTrend")
    @Operation(summary = "직급별 인건비 지출 추이", description = "연도별로 직급별 총 인건비(급여 + 성과급) 추이 반환")
    public ResponseEntity<List<SalaryByGradeTrendResponse>> getSalaryGradeTrend() {
        List<Object[]> rows = adjustSubjectRepository.getSalaryTrendByGrade();

        List<SalaryByGradeTrendResponse> result = rows.stream()
            .map(row -> new SalaryByGradeTrendResponse(
                ((Number)row[0]).intValue(), // year
                (String)row[1],              // grade
                ((Number)row[2]).longValue() // total_salary
            ))
            .toList();

        return ResponseEntity.ok(result);
    }
}
