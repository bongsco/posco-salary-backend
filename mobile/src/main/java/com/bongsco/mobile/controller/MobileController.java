package com.bongsco.mobile.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bongsco.mobile.dto.response.AdjustDetailResponse;
import com.bongsco.mobile.dto.response.AdjustListResponse;
import com.bongsco.mobile.dto.response.ChartResponse;
import com.bongsco.mobile.service.MobileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile")
public class MobileController {
    private final MobileService mobileService;

    @GetMapping("/chartData")
    public ResponseEntity<List<ChartResponse>> getEmployeeChartData(
        @RequestHeader("email") String email) {
        String empNum = email.replace("@bongsco.com", "");
        return ResponseEntity.ok(mobileService.getChartData(empNum));
    }

    @GetMapping("/adjusts")
    public ResponseEntity<AdjustListResponse> getAdjustList(
        @RequestParam Integer pageNum,
        @RequestParam Integer pageSize,
        @RequestHeader("email") String email) {
        String empNum = email.replace("@bongsco.com", "");
        return ResponseEntity.ok(mobileService.getAdjustList(empNum, pageNum, pageSize));
    }

    @GetMapping("/details/{adjustId}")
    public ResponseEntity<AdjustDetailResponse> getAdjustDetail(@PathVariable Long adjustId,
        @RequestHeader("email") String email) {
        String empNum = email.replace("@bongsco.com", "");
        return ResponseEntity.ok(mobileService.getAdjustDetailList(adjustId, empNum));
    }
}
