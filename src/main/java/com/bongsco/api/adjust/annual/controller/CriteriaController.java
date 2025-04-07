package com.bongsco.api.adjust.annual.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.api.adjust.annual.dto.request.PaybandCriteriaModifyRequest;
import com.bongsco.api.adjust.annual.dto.request.RankIncrementRateRequest;
import com.bongsco.api.adjust.annual.dto.request.SubjectCriteriaRequest;
import com.bongsco.api.adjust.annual.dto.response.PaybandCriteriaConfigListResponse;
import com.bongsco.api.adjust.annual.dto.response.SubjectCriteriaResponse;
import com.bongsco.api.adjust.annual.entity.PaybandCriteria;
import com.bongsco.api.adjust.annual.entity.SalaryIncrementRateByRank;
import com.bongsco.api.adjust.annual.service.CriteriaService;

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

    // TODO("Correct return type and logic")
    @PatchMapping("/{adj_info_id}/increment")
    public ResponseEntity<RankIncrementRateRequest> updateRankIncrementRates(
        @PathVariable(name = "adj_info_id") Long adjInfoId,
        @Valid @RequestBody RankIncrementRateRequest request) {
        List<SalaryIncrementRateByRank> savedData = criteriaService.updateRankIncrementRates(adjInfoId, request);
        return ResponseEntity.ok(request);
    }

    // TODO("Correct return type and logic")
    @PostMapping("/{adj_info_id}/increment")
    public ResponseEntity<RankIncrementRateRequest> saveRankIncrementRates(
        @PathVariable(name = "adj_info_id") Long adjInfoId,
        @Valid @RequestBody RankIncrementRateRequest request) {
        List<SalaryIncrementRateByRank> savedData = criteriaService.saveRankIncrementRates(adjInfoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    // TODO("Correct return type and logic")
    @GetMapping("/{adj_info_id}/payband")
    public ResponseEntity<PaybandCriteriaConfigListResponse> getPaybandCriteria(
        @PathVariable(name = "adj_info_id") Long adjInfoId) {

        return ResponseEntity.status(HttpStatus.CREATED).body(criteriaService.getPaybandCriteria(adjInfoId));
    }

    // TODO("Correct return type and logic")
    @PatchMapping("/{adj_info_id}/payband")
    public ResponseEntity<String> updatePaybandCriteria(
        @Valid @RequestBody PaybandCriteriaModifyRequest request) {
        List<PaybandCriteria> updatedData = criteriaService.updatePaybandCriteria(request);
        return ResponseEntity.ok("TODO: fix payband");
    }
}
