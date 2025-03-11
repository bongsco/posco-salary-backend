package com.bongsco.poscosalarybackend.adjust.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.poscosalarybackend.adjust.dto.response.EmployeeResponse;
import com.bongsco.poscosalarybackend.adjust.service.EmployeeService;

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
    private final EmployeeService employeeService;

    @Operation(summary = "test", description = "test")
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
        @RequestParam(value = "searchKey", required = false) String searchKey) {

        if (searchKey != null) {
            return ResponseEntity.status(HttpStatus.OK).body(employeeService.findOne(adjInfoId, searchKey));
        }

        return ResponseEntity.status(HttpStatus.OK).body(employeeService.findAll(adjInfoId));
    }
}