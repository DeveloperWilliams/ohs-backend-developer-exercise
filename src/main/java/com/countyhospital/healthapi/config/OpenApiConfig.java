package com.countyhospital.healthapi.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.environment:local}")
    private String environment;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("County Hospital Health API")
                        .description(String.join("\n",
                                "REST API for managing patient records and encounters for County Hospital.",
                                "This API provides comprehensive healthcare data management including:",
                                "- Patient registration and management",
                                "- Encounter (visit) tracking",
                                "- Advanced patient search capabilities",
                                "- Secure data access",
                                "",
                                "## Key Features",
                                "- **Patient Management**: Full CRUD operations for patient records",
                                "- **Encounter Tracking**: Record and manage patient visits",
                                "- **Advanced Search**: Flexible patient search by multiple criteria",
                                "- **Validation**: Comprehensive input validation and error handling",
                                "- **Security**: API key authentication (optional)",
                                "- **Documentation**: Interactive API documentation",
                                "",
                                "## Healthcare Standards",
                                "- FHIR-inspired data models",
                                "- ISO date formats",
                                "- Standard gender codes (MALE, FEMALE, OTHER, UNKNOWN)",
                                "- Encounter classification (INPATIENT, OUTPATIENT, EMERGENCY, VIRTUAL)"
                        ))
                        .version(appVersion)
                        .contact(new Contact()
                                .name("County Hospital IT Support")
                                .email("it-support@countyhospital.org")
                                .url("https://www.countyhospital.org"))
                        .license(new License()
                                .name("Hospital Internal Use")
                                .url("https://www.countyhospital.org/license")))
                .servers(List.of(
                        new Server()
                                .url(environment.equals("prod") ? 
                                        "https://api.countyhospital.org" : 
                                        "http://localhost:8080")
                                .description(environment.equals("prod") ? 
                                        "Production Server" : 
                                        "Local Development Server"),
                        new Server()
                                .url("https://staging.countyhospital.org")
                                .description("Staging Server")
                ));
    }
}