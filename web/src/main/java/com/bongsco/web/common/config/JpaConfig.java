package com.bongsco.web.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing  // ✅ Auditing 활성화
public class JpaConfig {
}