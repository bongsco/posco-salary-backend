package com.bongsco.api.adjust.annual.controller;

import static com.bongsco.api.adjust.common.util.ParseSorts.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.api.adjust.annual.dto.request.PaybandApplyListUpdateRequest;
import com.bongsco.api.adjust.annual.dto.response.MainResultResponses;
import com.bongsco.api.adjust.annual.dto.response.PaybandSubjectResponse;
import com.bongsco.api.adjust.annual.service.AdjustSubjectService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "MainAdjAPI", description = "Department 관련 API 모음")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/adjust/{adjustId}/main")
public class MainAdjustController {
    private final AdjustSubjectService adjustSubjectService;

    @Operation(summary = "payband 대상자", description = "payband 대상자 반환")
    @GetMapping("/payband/subjects")
    public ResponseEntity<PaybandSubjectResponse> getPaybandSubjects(
        @PathVariable("adjustId") Long adjustId
    ) {
        adjustSubjectService.calculateSalaryAndBonus(adjustId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(adjustSubjectService.getBothUpperLowerSubjects(adjustId));
    }

    @Operation(summary = "payband 여부 수정", description = "payband 여부 수정")
    @PatchMapping("/payband/subjects")
    public ResponseEntity<Void> updatePaybandSubjects(
        @Valid @RequestBody PaybandApplyListUpdateRequest paybandApplyListUpdateRequest
    ) {
        adjustSubjectService.updateSubjectPaybandApplication(paybandApplyListUpdateRequest.getUpdatedSubjects());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "정기 연봉 조정", description = "마지막 페이지, 계산값 모두 보여줌")
    @GetMapping("/annual-adj")
    public ResponseEntity<MainResultResponses> showResult(@PathVariable("adjustId") Long adjustId,
        @RequestParam(value = "filterEmpNum", required = false) String filterEmpNum,
        @RequestParam(value = "filterName", required = false) String filterName,
        @RequestParam(value = "filterGrade", required = false) String filterGrade,
        @RequestParam(value = "filterDepartment", required = false) String filterDepartment,
        @RequestParam(value = "filterRank", required = false) String filterRank,
        @RequestParam(value = "sorts", required = false) String sorts,
        @RequestParam(value = "pageNumber", required = false, defaultValue = "1") @Min(1) Integer pageNumber,
        @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize

    ) throws JsonProcessingException {
        adjustSubjectService.changeIncrementRate(adjustId);
        return ResponseEntity.ok(
            adjustSubjectService.getFinalResult(adjustId, filterEmpNum, filterName, filterGrade, filterDepartment,
                filterRank, extractSorts(sorts), pageNumber - 1, pageSize));
    }
}
