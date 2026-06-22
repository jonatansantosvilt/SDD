package com.viltgroup.statusmetro.linestatus.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** OpenAPI/Swagger metadata for the Line Status API (Constitution Principle IV). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lineStatusOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Status Metrô — Line Status API")
                .version("1.0.0")
                .description("Normalized, last-known-good snapshot of São Paulo Metrô and CPTM line "
                        + "statuses, refreshed from the public CCR mobility source every 60 seconds."));
    }
}
