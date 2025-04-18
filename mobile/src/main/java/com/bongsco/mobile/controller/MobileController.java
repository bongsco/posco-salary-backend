package com.bongsco.mobile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/{employeeId}")
public class MobileController {
    @GetMapping
    public ResponseEntity<String> getEmployeeData(@PathVariable String employeeId) {
        return ResponseEntity.ok("안녕!");
    }
}
