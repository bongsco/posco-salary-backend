package com.bongsco.poscosalarybackend.adjust.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.poscosalarybackend.adjust.dto.request.ChangedEmployeeRequest;
import com.bongsco.poscosalarybackend.adjust.dto.response.EmployeeResponse;
import com.bongsco.poscosalarybackend.adjust.service.AdjSubjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Preprocess API", description = "Preprocess 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/preprocess")
public class PreprocessController {
    private final AdjSubjectService adjSubjectService;

    @Operation(summary = "대상자 편성 페이지 GET API", description = "대상자 편성 페이지에서 조정 대상자와 비대상자 정보 반환")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "요청 성공, 'test' 문자열 반환"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{adj_info_id}/employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(
        @PathVariable("adj_info_id") long adjInfoId,
        @RequestParam(value = "searchKey", required = false) String searchKey
    ) {
        if (searchKey != null) {
            return ResponseEntity.status(HttpStatus.OK).body(adjSubjectService.findOne(adjInfoId, searchKey));
        }

        return ResponseEntity.status(HttpStatus.OK).body(adjSubjectService.findAll(adjInfoId));
    }

    @Operation(summary = "대상자 편성 페이지 POST API", description = "대상자 편성 페이지에서 변하는 조정 정보를 업데이트")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "요청 성공, 'test' 문자열 반환"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "검색한 정보가 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("{adj_info_id}/employees")
    public ResponseEntity<Map<String, String>> updateEmployees(
        @PathVariable("adj_info_id") long adjInfoId,
        @RequestBody ChangedEmployeeRequest ChangedEmployeeRequest
    ) {
        adjSubjectService.updateEmployeeSubjectUse(adjInfoId, ChangedEmployeeRequest);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully changed");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}