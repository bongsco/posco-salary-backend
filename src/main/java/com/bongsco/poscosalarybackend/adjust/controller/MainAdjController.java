package com.bongsco.poscosalarybackend.adjust.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.poscosalarybackend.adjust.dto.MainAdjPaybandCriteriaResponse;
import com.bongsco.poscosalarybackend.adjust.service.PaybandCriteriaService;
import com.bongsco.poscosalarybackend.global.dto.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
@Tag(name = "Department API", description = "Department 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main-adj")
public class MainAdjController {
    private final PaybandCriteriaService paybandCriteriaService;

    @Operation(summary = "payband 기준", description = "직급별 payband 기준 표 반환")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "요청 성공, payband criteria 반환"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{adj_info_id}/payband/criteria")
    public ResponseEntity<JsonResult<List<MainAdjPaybandCriteriaResponse>>> getPaybandCriteria(
        @PathVariable("adj_info_id") Long adjInfoId) {

        List<MainAdjPaybandCriteriaResponse> mainAdjPaybandCriteriaResponse = paybandCriteriaService.findAllPaybandCriteria(
            adjInfoId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(JsonResult.success(mainAdjPaybandCriteriaResponse));
    }
}
