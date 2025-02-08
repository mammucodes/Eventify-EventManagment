package com.example.eventifyeventmanagment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Eventify API",
                version = "1.0",
                description = "API documentation for Eventify application"
        )
)
public class SwaggerConfig {
}
