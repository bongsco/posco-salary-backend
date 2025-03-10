package com.bongsco.poscosalarybackend.user.controller;

import com.bongsco.poscosalarybackend.user.domain.Department;
import com.bongsco.poscosalarybackend.user.dto.DepartmentDto;
import com.bongsco.poscosalarybackend.user.repository.DepartmentRepository;
import com.bongsco.poscosalarybackend.user.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name="Department API", description = "Department 관련 API 모음")
@RestController
@RequestMapping("/api/v1/department")
public class DepartmentController {
    private final DepartmentService departmentService;
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Operation(summary = "test",description = "test")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공, 'test' 문자열 반환"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/test")
    public ResponseEntity<List<DepartmentDto>> test() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(departmentService.getAllDepart());
    }
}
