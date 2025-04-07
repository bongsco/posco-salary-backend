package com.bongsco.poscosalarybackend.adjust.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.poscosalarybackend.adjust.domain.PaybandCriteria;
import com.bongsco.poscosalarybackend.adjust.domain.RankIncrementRate;
import com.bongsco.poscosalarybackend.adjust.dto.request.PaybandCriteriaDeleteRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.PaybandCriteriaRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.RankIncrementRateRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.SubjectCriteriaRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.SubjectCriteriaResponse;
import com.bongsco.poscosalarybackend.adjust.service.CriteriaService;
import com.bongsco.poscosalarybackend.global.dto.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@Tag(name = "기준 설정 관련 API", description = "대상자 기준, 기준연봉, 평가차등 등등..")
@RestController
@RequiredArgsConstructor
@RequestMapping("/criteria")
public class CriteriaController {

    private final CriteriaService criteriaService;

    @Operation(summary = "대상자 기준 설정 PATCH API", description = "대상자 기준 설정 수정된 값 전달")
    @PatchMapping("/{adj_info_id}/subject")
    public ResponseEntity<JsonResult<SubjectCriteriaResponse>> saveSubjectCriteria(
        @PathVariable(name = "adj_info_id") Long adjInfoId,
        @Valid @RequestBody SubjectCriteriaRequest subjectCriteriaRequest
    ) {
        SubjectCriteriaResponse res = criteriaService.updateSubjectCriteria(adjInfoId, subjectCriteriaRequest);
        return ResponseEntity.ok(JsonResult.success(res));
    }

    @PatchMapping("/{adj_info_id}/increment")
    public ResponseEntity<JsonResult<RankIncrementRateRequest>> updateRankIncrementRates(
        @PathVariable(name = "adj_info_id") Long adjInfoId,
        @Valid @RequestBody RankIncrementRateRequest request) {
        List<RankIncrementRate> savedData = criteriaService.updateRankIncrementRates(adjInfoId, request);
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @PostMapping("/{adj_info_id}/increment")
    public ResponseEntity<JsonResult<RankIncrementRateRequest>> saveRankIncrementRates(
        @PathVariable(name = "adj_info_id") Long adjInfoId,
        @Valid @RequestBody RankIncrementRateRequest request) {
        List<RankIncrementRate> savedData = criteriaService.saveRankIncrementRates(adjInfoId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(JsonResult.success(request));
    }

    @PostMapping("/{adj_info_id}/payband")
    public ResponseEntity<JsonResult<List<PaybandCriteria>>> savePaybandCriteria(
        @PathVariable(name = "adj_info_id") Long adjInfoId,
        @Valid @RequestBody PaybandCriteriaRequest request) {
        List<PaybandCriteria> savedData = criteriaService.savePaybandCriteria(adjInfoId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(JsonResult.success(savedData));
    }

    @PatchMapping("/{adj_info_id}/payband")
    public ResponseEntity<JsonResult<List<PaybandCriteria>>> updatePaybandCriteria(
        @PathVariable(name = "adj_info_id") Long adjInfoId,
        @Valid @RequestBody PaybandCriteriaRequest request) {
        List<PaybandCriteria> updatedData = criteriaService.updatePaybandCriteria(adjInfoId, request);
        return ResponseEntity.ok(JsonResult.success(updatedData));
    }

    @DeleteMapping("/{adj_info_id}/payband")
    public ResponseEntity<Void> deletePaybandCriteria(
        @Valid @RequestBody PaybandCriteriaDeleteRequest request) {
        criteriaService.deletePaybandCriteria(request);
        return ResponseEntity.noContent().build();
    }
}
