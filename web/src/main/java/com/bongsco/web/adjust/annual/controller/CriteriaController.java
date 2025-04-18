package com.bongsco.web.adjust.annual.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.web.adjust.annual.dto.request.PaybandCriteriaModifyRequest;
import com.bongsco.web.adjust.annual.dto.request.PaymentRateUpdateRequest;
import com.bongsco.web.adjust.annual.dto.request.SubjectCriteriaRequest;
import com.bongsco.web.adjust.annual.dto.response.PaybandCriteriaConfigListResponse;
import com.bongsco.web.adjust.annual.dto.response.PaymentRateResponse;
import com.bongsco.web.adjust.annual.dto.response.PaymentRateUpdateResponse;
import com.bongsco.web.adjust.annual.dto.response.SubjectCriteriaResponse;
import com.bongsco.web.adjust.annual.service.AdjustSubjectService;
import com.bongsco.web.adjust.annual.service.CriteriaService;
import com.bongsco.web.adjust.common.service.AdjustStepService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "기준 설정 관련 API", description = "대상자 기준, 기준연봉, 평가차등 등등..")
@RestController
@RequiredArgsConstructor
@RequestMapping("/adjust/{adjustId}/criteria")
public class CriteriaController {
    private final CriteriaService criteriaService;
    private final AdjustSubjectService adjustSubjectService;
    private final AdjustStepService adjustStepService;

    @Operation(summary = "대상자 기준 설정 GET API", description = "대상자 기준 설정 기존 값 전달")
    @GetMapping("/subject")
    public ResponseEntity<SubjectCriteriaResponse> getSubjectCriteria(
        @PathVariable(name = "adjustId") Long adjustId
    ) {
        SubjectCriteriaResponse response = criteriaService.getSubjectCriteria(adjustId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "대상자 기준 설정 PATCH API", description = "대상자 기준 설정 수정된 값 전달")
    @PatchMapping("/subject")
    public ResponseEntity<SubjectCriteriaResponse> saveSubjectCriteria(
        @PathVariable(name = "adjustId") Long adjustId,
        @Valid @RequestBody SubjectCriteriaRequest subjectCriteriaRequest
    ) {
        SubjectCriteriaResponse res = criteriaService.updateSubjectCriteria(adjustId, subjectCriteriaRequest);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/paymentrate")
    public ResponseEntity<?> getPaymentRate(
        @PathVariable Long adjustId
    ) {
        PaymentRateResponse response = criteriaService.getPaymentRate(adjustId);
        return ResponseEntity.ok(Map.of(
            "message", "success",
            "data", response
        ));
    }

    @PatchMapping("/paymentrate")
    public ResponseEntity<?> updatePaymentRate(
        @PathVariable Long adjustId,
        @Valid @RequestBody PaymentRateUpdateRequest request
    ) {
        List<String> updatedGrades = criteriaService.updatePaymentRate(adjustId, request);
        adjustSubjectService.initializeIsPaybandApplied(adjustId);
        adjustStepService.resetMain(adjustId);
        return ResponseEntity.ok(new PaymentRateUpdateResponse(updatedGrades));
    }

    @Operation(summary = "payband 기준 설정 GET API", description = "payband 기준 가져옴")
    @GetMapping("/payband")
    public ResponseEntity<PaybandCriteriaConfigListResponse> getPaybandCriteria(
        @PathVariable(name = "adjustId") Long adjustId) {

        return ResponseEntity.ok(criteriaService.getPaybandCriteria(adjustId));
    }

    @Operation(summary = "payband 기준 설정 PATCH API", description = "payband 기준 설정 수정된 값 반영")
    @PatchMapping("/payband")
    public ResponseEntity<Void> updatePaybandCriteria(
        @Valid @RequestBody PaybandCriteriaModifyRequest request,
        @PathVariable(name = "adjustId") Long adjustId) {

        criteriaService.updatePaybandCriteria(request);
        adjustSubjectService.initializeIsPaybandApplied(adjustId);
        adjustStepService.resetMain(adjustId);
        return ResponseEntity.noContent().build();
    }
}
