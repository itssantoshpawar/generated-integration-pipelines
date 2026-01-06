package com.osb.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main Spring Boot application for OSB Pipeline Orchestration.
 */
@SpringBootApplication
@EnableConfigurationProperties
public class OsbPipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(OsbPipelineApplication.class, args);
    }
}
