package com.bongsco.api.adjust.annual.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.api.adjust.annual.dto.request.AdjustPostRequest;
import com.bongsco.api.adjust.annual.dto.request.AdjustUpdateRequest;
import com.bongsco.api.adjust.annual.dto.response.AdjustResponse;
import com.bongsco.api.adjust.annual.service.AdjustService;
import com.bongsco.api.global.dto.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "Main API", description = "Main 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/adjust")
public class AdjustController {
    private final AdjustService adjustService;

    @Operation(summary = "조정 정보 조회", description = "startYear와 endYear를 통해 조정 정보를 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<JsonResult<AdjustResponse>> getAdjustInfo(
        @RequestParam(value = "startYear", required = false) Long startYear,
        @RequestParam(value = "endYear", required = false) Long endYear) {
        AdjustResponse response = adjustService.getAdjustInfo(startYear, endYear);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "조정 정보 수정")
    @PatchMapping("/{adjustId}")
    public ResponseEntity<JsonResult<Map<String, String>>> updateAdjustInfo(
        @PathVariable @Min(1) Long adjustId,
        @Valid @RequestBody AdjustUpdateRequest updateRequest) {

        adjustService.updateAdjust(adjustId, updateRequest);

        return ResponseEntity.ok(JsonResult.success(Map.of("message", "Successfully changed")));
    }

    @Operation(summary = "조정 정보 삭제", description = "조정 정보를 삭제합니다.")
    @DeleteMapping("/{adjustId}")
    public ResponseEntity<Void> deleteAdjustInfo(@PathVariable @Min(1) Long adjustId) {
        adjustService.deleteAdjustInfo(adjustId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "조정 정보 추가", description = "조정 정보를 추가합니다.")
    @PostMapping
    public ResponseEntity<JsonResult<Map<String, String>>> postAdjustInfo(
        @Valid @RequestBody AdjustPostRequest postRequest) {
        adjustService.postAdjustInfo(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(JsonResult.success(Map.of("message", "Successfully post")));
    }
}
