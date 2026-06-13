package com.cliniqo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI cliniqoOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cliniqo F01 Foundation API")
                        .version("0.1.0")
                        .description("Backend foundation contracts for authentication, tenancy, audit, and safe errors."));
    }
}
