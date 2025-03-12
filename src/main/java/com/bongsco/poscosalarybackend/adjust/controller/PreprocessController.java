package com.bongsco.poscosalarybackend.adjust.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedHighPerformGroupEmployeeRequest;
import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedSubjectUseEmployeeRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.CompensationEmployeeResponse;
import com.bongsco.poscosalarybackend.adjust.dto.response.EmployeeResponse;
import com.bongsco.poscosalarybackend.adjust.service.AdjSubjectService;
import com.bongsco.poscosalarybackend.global.dto.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Preprocess API", description = "Preprocess 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/preprocess")
public class PreprocessController {
    private final AdjSubjectService adjSubjectService;

    @Operation(summary = "대상자 편성 페이지 GET API", description = "대상자 편성 페이지에서 조정 대상자와 비대상자 정보 반환")
    @GetMapping("/{adj_info_id}/employees")
    public ResponseEntity<JsonResult<List<EmployeeResponse>>> getEmployees(
        @PathVariable("adj_info_id") long adjInfoId,
        @RequestParam(value = "searchKey", required = false) String searchKey
    ) {
        if (searchKey != null) {
            return ResponseEntity.status(HttpStatus.OK)
                .body(JsonResult.success(adjSubjectService.findBySearchKey(adjInfoId, searchKey)));
        }

        return ResponseEntity.status(HttpStatus.OK).body(JsonResult.success(adjSubjectService.findAll(adjInfoId)));
    }

    @Operation(summary = "대상자 편성 페이지 POST API", description = "대상자 편성 페이지에서 변하는 조정 정보를 업데이트")
    @PostMapping("/{adj_info_id}/employees")
    public ResponseEntity<JsonResult<String>> updateSubjectUseEmployee(
        @PathVariable("adj_info_id") long adjInfoId,
        @RequestBody ChangedSubjectUseEmployeeRequest ChangedSubjectUseEmployeeRequest
    ) {
        adjSubjectService.updateSubjectUseEmployee(adjInfoId, ChangedSubjectUseEmployeeRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(JsonResult.success("Successfully changed"));
    }

    @Operation(summary = "고성과조직 가산 여부 페이지 GET API", description = "고성과조직 가산 여부 테이블 조회")
    @GetMapping("/{adj_info_id}/compensation")
    public ResponseEntity<List<CompensationEmployeeResponse>> getCompensationTable(
        @PathVariable("adj_info_id") long adjInfoId,
        @RequestParam(value = "searchKey", required = false) String searchKey
    ) {
        if (searchKey != null) {
            return ResponseEntity.status(HttpStatus.OK)
                .body(adjSubjectService.findCompensationBySearchKey(adjInfoId, searchKey));
        }

        return ResponseEntity.status(HttpStatus.OK).body(adjSubjectService.findCompensationAll(adjInfoId));
    }

    @Operation(summary = "고성과조직 가산 여부 페이지 POST API", description = "고성과조직 가산 여부 업데이트")
    @PostMapping("/{adj_info_id}/compensation")
    public ResponseEntity<JsonResult<String>> updateHighPerformGroupEmployees(
        @PathVariable("adj_info_id") long adjInfoId,
        @RequestBody ChangedHighPerformGroupEmployeeRequest changedHighPerformGroupEmployeeRequest
    ) {
        adjSubjectService.updateHighPerformGroupEmployee(adjInfoId, changedHighPerformGroupEmployeeRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(JsonResult.success("Successfully changed"));
    }
}