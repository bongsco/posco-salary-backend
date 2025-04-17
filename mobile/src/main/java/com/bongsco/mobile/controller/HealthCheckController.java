package com.bongsco.mobile.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/mobile")
public class HealthCheckController {
    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        // response를 만드는 방식
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("app", "mobile");
        return response;
    }
}