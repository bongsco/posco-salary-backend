package com.bongsco.mobile.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
