package com.countyhospital.healthapi.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class OpenApiConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.environment:local}")
    private String environment;

    @Bean
    public OpenAPI customOpenAPI() {

        //  order
        List<Tag> tags = List.of(
                new Tag()
                        .name("01 - Patient Management")
                        .description("APIs for managing patient records"),

                new Tag()
                        .name("02 - Encounter Management")
                        .description("APIs for managing patient encounters")
        );

        // Servers
        List<Server> servers = List.of(
                new Server()
                        .url(environment.equals("prod")
                                ? "https://api.countyhospital.org"
                                : "http://localhost:8080")
                        .description(environment.equals("prod")
                                ? "Production Server"
                                : "Local Development Server"),

                new Server()
                        .url("https://staging.countyhospital.org")
                        .description("Staging Server")
        );

        //info
        Info info = new Info()
                .title("County Hospital Health API")
                .version(appVersion)
                .description(
                        """
                        REST API for managing patient records and encounters.

                        ## Key Features
                        - Patient Management CRUD
                        - Encounter Tracking
                        - Comprehensive Searching
                        - Input Validation & Error Handling
                        - Optional API Key Security
                        - Swagger Interactive Documentation

                        ## Healthcare Standards
                        - FHIR-inspired data models
                        - ISO date formats
                        - Gender: MALE, FEMALE, OTHER, UNKNOWN
                        - EncounterClass: INPATIENT, OUTPATIENT, EMERGENCY, VIRTUAL
                        """
                )
                .license(
                        new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")
                );

        // Final OpenAPI object
        return new OpenAPI()
                .info(info)
                .servers(servers)
                .tags(tags);  
    }
}
