package com.bongsco.api.adjust.annual.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.api.adjust.annual.dto.request.PaybandApplyListUpdateRequest;
import com.bongsco.api.adjust.annual.dto.response.PaybandSubjectResponse;
import com.bongsco.api.adjust.annual.service.AdjustSubjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "MainAdjAPI", description = "Department 관련 API 모음")
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

    @Operation(summary = "기준 연봉과 성과금 계산", description = "사전 작업에서 입력한 내용을 기준으로 일괄적으로 기준 연봉과 성과금 계산, payband 넘어가기전에 넣어줘야함")
    @PatchMapping("/calculate-salary")
    public ResponseEntity<Void> calculateSalary(@PathVariable("adjustId") Long adjustId
    ) {
        adjustSubjectService.calculateSalaryAndBonus(adjustId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "기준연봉 인상률 변경", description = "payband가 반영되어서 최종 연봉이 나왔을 때 인상률이 바뀜")
    @PatchMapping("/increment-rate")
    public ResponseEntity<Void> incrementRate(@PathVariable("adjustId") Long adjustId) {
        adjustSubjectService.changeIncrementRate(adjustId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "정기 연봉 조정", description = "마지막 페이지, 계산값 모두 보여줌")
    @PatchMapping("/{adj_info_id}/annual-adj")
    public ResponseEntity<Void> showResult(@PathVariable("adj_info_id") Long adjInfoId,
        @RequestParam(value = "searchKey", required = false) String searchKey
    ) {
        // TODO("Implement endpoint")

        return ResponseEntity.noContent().build();
    }
}
