package com.bongsco.poscosalarybackend.adjust.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.poscosalarybackend.adjust.dto.response.AdjustResponse;
import com.bongsco.poscosalarybackend.adjust.service.AdjustService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Main API", description = "Main 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class AdjustController {
    private final AdjustService adjustService;

    @Operation(summary = "조정 정보 조회", description = "startYear와 endYear를 통해 조정 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "요청 성공, 'test' 문자열 반환"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<AdjustResponse> getAdjustInfo(
        @RequestParam(value = "startYear", required = false) Long startYear,
        @RequestParam(value = "endYear", required = false) Long endYear) {

        AdjustResponse response = adjustService.getAdjustInfo(startYear, endYear);

        return ResponseEntity.ok(response);
    }
}
