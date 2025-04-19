package com.bongsco.mobile.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.mobile.domain.AdjustType;
import com.bongsco.mobile.dto.response.AdjustDetailResponse;
import com.bongsco.mobile.dto.response.AdjustInfoResponse;
import com.bongsco.mobile.dto.response.AdjustListResponse;
import com.bongsco.mobile.dto.response.ChartResponse;
import com.bongsco.mobile.service.MobileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/{employeeId}")
public class MobileController {
    private final MobileService mobileService;

    @GetMapping("/chartData")
    public ResponseEntity<List<ChartResponse>> getEmployeeChartData(@PathVariable Long employeeId) {
        return ResponseEntity.ok(mobileService.getChartData(employeeId));
    }

    @GetMapping("/adjusts")
    public ResponseEntity<AdjustListResponse> getAdjustList(
        @PathVariable Long employeeId,
        @RequestParam Integer pageNum,
        @RequestParam Integer pageSize) {
        return ResponseEntity.ok(mobileService.getAdjustList(employeeId, pageNum, pageSize));
    }

    @GetMapping("/details/{adjustId}")
    public ResponseEntity<AdjustDetailResponse> getAdjustDetail(@PathVariable Long adjustId, @PathVariable Long employeeId) {
        return ResponseEntity.ok(mobileService.getAdjustDetailList(adjustId, employeeId));
    }

}
