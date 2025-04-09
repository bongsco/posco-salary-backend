package com.bongsco.api.adjust.annual.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.api.adjust.annual.dto.request.ChangedHighPerformGroupEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.request.ChangedSubjectUseEmployeeRequest;
import com.bongsco.api.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.api.adjust.annual.dto.response.HpoEmployeesResponse;
import com.bongsco.api.adjust.annual.service.AdjustSubjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Preprocess API", description = "Preprocess 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/adjust/{adjustId}/preparation")
public class PreparationController {
    private final AdjustSubjectService adjSubjectService;

    @Operation(summary = "대상자 편성 페이지 GET API", description = "대상자 편성 페이지에서 조정 대상자와 비대상자 정보 반환")
    @GetMapping("/{adj_info_id}/employees")
    // TODO("Replace response type to single DTO Type")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(
        @PathVariable("adj_info_id") Long adjInfoId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adjSubjectService.findAll(adjInfoId));
    }

    @Operation(summary = "대상자 편성 페이지 PATCH API", description = "대상자 편성 페이지에서 변하는 조정 정보를 업데이트")
    @PatchMapping("/{adj_info_id}/employees")
    public ResponseEntity<Void> updateSubjectUseEmployee(
        @PathVariable("adj_info_id") Long adjInfoId,
        @Valid @RequestBody ChangedSubjectUseEmployeeRequest ChangedSubjectUseEmployeeRequest
    ) {
        adjSubjectService.updateSubjectUseEmployee(adjInfoId, ChangedSubjectUseEmployeeRequest);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "고성과조직 가산 여부 페이지 GET API", description = "고성과조직 가산 여부 테이블 조회")
    @GetMapping("/high-performance")
    public ResponseEntity<HpoEmployeesResponse> getCompensationTable(
        @PathVariable("adjustId") Long adjustId
    ) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(adjSubjectService.findHpoEmployees(adjustId));
    }

    @Operation(summary = "고성과조직 가산 여부 페이지 PATCH API", description = "고성과조직 가산 여부 업데이트")
    @PatchMapping("/high-performance")
    public ResponseEntity<Void> updateHighPerformGroupEmployees(
        @PathVariable("adjustId") Long adjustId,
        @Valid @RequestBody ChangedHighPerformGroupEmployeeRequest changedHighPerformGroupEmployeeRequest
    ) {
        adjSubjectService.updateHighPerformGroupEmployee(adjustId, changedHighPerformGroupEmployeeRequest);

        return ResponseEntity.noContent().build();
    }
}