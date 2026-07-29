package com.goktug.wallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI walletOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wallet Payment Backend API")
                        .description("Event-driven payment and wallet backend with double-entry ledger, "
                                + "JWT authentication, and ML-based fraud detection.")
                        .version("1.0"));
    }
}