package com.bongsco.poscosalarybackend.adjust.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.poscosalarybackend.adjust.dto.request.AdjInfoDeleteRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.AdjInfoPostRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.AdjInfoUpdateRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.AdjustResponse;
import com.bongsco.poscosalarybackend.adjust.service.AdjustService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Main API", description = "Main 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class AdjustController {
    private final AdjustService adjustService;

    @Operation(summary = "조정 정보 조회", description = "startYear와 endYear를 통해 조정 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<AdjustResponse> getAdjustInfo(
        @RequestParam(value = "startYear", required = false) Long startYear,
        @RequestParam(value = "endYear", required = false) Long endYear) {

        AdjustResponse response = adjustService.getAdjustInfo(startYear, endYear);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "조정 정보 수정", description = "조정 정보를 수정합니다.")
    @PatchMapping
    public ResponseEntity<Map<String, String>> updateAdjustInfo(
        @RequestBody AdjInfoUpdateRequest updateRequest) {

        adjustService.updateAdjustInfo(updateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Successfully changed"));
    }

    @Operation(summary = "조정 정보 삭제", description = "조정 정보를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteAdjustInfo(
        @RequestBody AdjInfoDeleteRequest deleteRequest) {

        adjustService.deleteAdjustInfo(deleteRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Successfully deleted"));
    }

    @Operation(summary = "조정 정보 추가", description = "조정 정보를 추가합니다.")
    @PostMapping
    public ResponseEntity<Map<String, String>> postAdjustInfo(
        @Valid @RequestBody AdjInfoPostRequest postRequest) {

        adjustService.postAdjustInfo(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Successfully post"));
    }
}
