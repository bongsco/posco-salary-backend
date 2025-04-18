package com.bongsco.web.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .components(new Components())
            .info(apiInfo())
            .servers(List.of(
                new Server().url("https://gbyelx4c2h.execute-api.ap-northeast-2.amazonaws.com")));
    }

    private Info apiInfo() {
        return new Info()
            .title("Bongsco Swagger")
            .description("Bongsco 서비스에 관한 REST API")
            .version("1.0.0");
    }
}
