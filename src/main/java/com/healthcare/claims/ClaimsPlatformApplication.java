package com.healthcare.claims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Healthcare Claims Processing Platform
 *
 * A HIPAA-compliant claims processing system built with:
 * - Spring Boot 3.x
 * - Apache Kafka for event streaming
 * - PostgreSQL for persistence
 * - JWT for authentication
 * - REST APIs for integration
 */
@SpringBootApplication
@EnableAsync
public class ClaimsPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClaimsPlatformApplication.class, args);
    }
}
