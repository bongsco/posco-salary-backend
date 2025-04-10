package com.bongsco.api.adjust.common.controller;

import static com.bongsco.api.adjust.common.util.ParseSorts.*;

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

import com.bongsco.api.adjust.annual.service.AdjustService;
import com.bongsco.api.adjust.common.domain.AdjustType;
import com.bongsco.api.adjust.common.dto.request.AdjustPostRequest;
import com.bongsco.api.adjust.common.dto.request.AdjustSearchRequest;
import com.bongsco.api.adjust.common.dto.request.AdjustUpdateRequest;
import com.bongsco.api.adjust.common.dto.response.AdjustResponse;
import com.bongsco.api.adjust.common.dto.response.SingleAdjustResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "Main(연봉조정조회 페이지) API", description = "Main(연봉조정조회) 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/adjust")
public class AdjustController {
    private final AdjustService adjustService;

    @Operation(summary = "조정 정보 조회", description = "페이지, 페이지별 행의 수, 필터, 정렬 조건들을 통해 조정 정보를 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<AdjustResponse> getAdjustInfo(
        @RequestParam(value = "page", required = true) Integer page,
        @RequestParam(value = "size", required = true) Integer size,
        @RequestParam(value = "year", required = false) Integer year,
        @RequestParam(value = "month", required = false) Integer month,
        @RequestParam(value = "adjType", required = false) AdjustType adjType,
        @RequestParam(value = "state", required = false) Boolean state,
        @RequestParam(value = "isSubmitted", required = false) Boolean isSubmitted,
        @RequestParam(value = "author", required = false) String author,
        @RequestParam(value = "sort", required = false) String sort
    ) throws JsonProcessingException {

        AdjustSearchRequest adjustSearchRequest = new AdjustSearchRequest(
            page, size, year, month, adjType, state, isSubmitted, author, extractSorts(sort)
        );

        AdjustResponse response = adjustService.getAdjustInfo(adjustSearchRequest);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "개별 조정 정보 조회", description = "특정 조정 ID를 가진 연봉 조정을 조회합니다.")
    @GetMapping("/{adjustId}")
    public ResponseEntity<SingleAdjustResponse> getAdjust(@PathVariable Long adjustId) {
        return ResponseEntity.ok(adjustService.getAdjust(adjustId));
    }

    @Operation(summary = "조정 정보 수정")
    @PatchMapping("/{adjustId}")
    public ResponseEntity<Map<String, String>> updateAdjustInfo(
        @PathVariable @Min(1) Long adjustId,
        @Valid @RequestBody AdjustUpdateRequest updateRequest) {

        adjustService.updateAdjust(adjustId, updateRequest);

        return ResponseEntity.ok(Map.of("message", "Successfully changed"));
    }

    @Operation(summary = "조정 정보 삭제", description = "조정 정보를 삭제합니다.")
    @DeleteMapping("/{adjustId}")
    public ResponseEntity<Void> deleteAdjustInfo(@PathVariable @Min(1) Long adjustId) {
        adjustService.deleteAdjustInfo(adjustId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "조정 정보 추가", description = "조정 정보를 추가합니다.")
    @PostMapping
    public ResponseEntity<Map<String, String>> postAdjustInfo(
        @Valid @RequestBody AdjustPostRequest postRequest) {
        adjustService.postAdjustInfo(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Successfully post"));
    }
}
