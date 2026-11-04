package com.example.banking_api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

// General API information shown at the top of Swagger UI (/swagger-ui.html)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankingOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Banking API")
                .version("1.0.0")
                .description("REST API for bank accounts: create accounts, "
                        + "deposit, withdraw and transfer money."));
    }
}
