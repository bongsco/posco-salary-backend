package com.bongsco.mobile.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${cors.origins}")
    private List<String> allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
            .allowedOrigins(allowedOrigins.toArray(String[]::new))
            .allowedMethods("GET", "OPTIONS") // 허용할 HTTP 메서드
            .allowedHeaders("*") // 모든 헤더 허용
            .allowCredentials(true) // 쿠키 등 인증 정보 허용
            .maxAge(3600); // preflight 캐시 시간 (초)
    }
}

