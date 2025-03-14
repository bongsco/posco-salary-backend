package com.bongsco.poscosalarybackend.adjust.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedSubjectListRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.MainAdjPaybandBothSubjectsResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.MainAdjPaybandCriteriaResponse;
import com.bongsco.poscosalarybackend.adjust.service.AdjSubjectService;
import com.bongsco.poscosalarybackend.adjust.service.PaybandCriteriaService;
import com.bongsco.poscosalarybackend.global.dto.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@Tag(name = "MainAdjAPI", description = "Department 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main-adj")
public class MainAdjController {
    private final PaybandCriteriaService paybandCriteriaService;
    private final AdjSubjectService adjSubjectService;

    @Operation(summary = "payband 기준", description = "직급별 payband 기준 표 반환")
    @GetMapping("/{adj_info_id}/payband/criteria")
    public ResponseEntity<JsonResult<MainAdjPaybandCriteriaResponse>> getPaybandCriteria(
        @PathVariable("adj_info_id") Long adjInfoId
    ) {
        MainAdjPaybandCriteriaResponse mainAdjPaybandCriteriaResponse = paybandCriteriaService.findAllPaybandCriteria(
            adjInfoId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(JsonResult.success(mainAdjPaybandCriteriaResponse));
    }

    @Operation(summary = "payband 대상자", description = "payband 대상자 반환")
    @GetMapping("/{adj_info_id}/payband/subjects")
    public ResponseEntity<JsonResult<MainAdjPaybandBothSubjectsResponse>> getPaybandSubjects(
        @PathVariable("adj_info_id") Long adjInfoId,
        @RequestParam(value = "searchKey", required = false) String searchKey
    ) {
        if (searchKey != null) {
            return ResponseEntity.status(HttpStatus.OK)
                .body(
                    JsonResult.success(adjSubjectService.getBothUpperLowerSubjectsWithSearchKey(adjInfoId, searchKey)));
        }

        return ResponseEntity.status(HttpStatus.OK)
            .body(JsonResult.success(adjSubjectService.getBothUpperLowerSubjects(adjInfoId)));
    }

    @Operation(summary = "payband 여부 수정", description = "payband 여부 수정")
    @PatchMapping("/{adj_info_id}/payband/subjects")
    public ResponseEntity<JsonResult<String>> modifyPaybandSubjects(
        @PathVariable("adj_info_id") Long adjInfoId,
        @Valid @RequestBody ChangedSubjectListRequest changedSubjectListRequest
    ) {
        changedSubjectListRequest
            .getChangedSubject()
            .stream()
            .forEach(subject -> {
                adjSubjectService.modifyAdjustSubject(subject.getAdjSubjectId(), subject.getPaybandUse());
            });

        return ResponseEntity.status(HttpStatus.OK)
            .body(JsonResult.success("Successfully changed"));
    }

    @Operation(summary = "기준 연봉 계산", description = "사전 작업에서 입력한 내용을 기준으로 일괄적으로 기준 연봉 계산, payband 넘어가기전에 넣어줘야함")
    @PatchMapping("/{adj_info_id}/calculate-salary")
    public ResponseEntity<JsonResult<String>> calculateSalary(@PathVariable("adj_info_id") Long adjInfoId) {
        adjSubjectService.calculateSalary(adjInfoId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(JsonResult.success("success"));
    }

    @Operation(summary = "성과금 계산", description = "사전 작업에서 입력한 내용을 기준으로 일괄적으로 성과금 계산, payband 넘어가기전에 넣어줘야함")
    @PatchMapping("/{adj_info_id}/calculate-add-payment")
    public ResponseEntity<JsonResult<String>> calculateAddPayment(@PathVariable("adj_info_id") Long adjInfoId) {
        adjSubjectService.calculateAddPayment(adjInfoId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(JsonResult.success("success"));
    }
}
