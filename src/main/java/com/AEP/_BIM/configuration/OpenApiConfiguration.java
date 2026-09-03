package com.AEP._BIM.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfiguration {
    @Bean
    OpenAPI conectaDoaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("API ConectaDoa")
                .description("API REST da AEP para cadastro de doações de alimentos")
                .version("v1"));
    }
}
