package com.healthcare.claims.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Healthcare Claims Platform API",
        version = "1.0.0",
        description = "HIPAA-compliant healthcare claims processing REST API. " +
            "Supports claim submission, processing, approval, and real-time Kafka event streaming.",
        contact = @Contact(
            name = "Shanmukha Sai Ram Tummuri",
            email = "shanmukhsairam84@gmail.com"
        )
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {
}
