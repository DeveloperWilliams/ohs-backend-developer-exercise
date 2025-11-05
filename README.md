# County Hospital Health API

A Development-ready digital health backend system for managing patient records and encounters, built with Spring Boot and following healthcare domain standards.
[https://health.backend.williamachuchi.com/](https://health.backend.williamachuchi.com/)

## Overview

This API provides comprehensive healthcare data management for county hospitals, including patient registration, encounter tracking, and advanced search capabilities. The system is designed with scalability, security, and healthcare compliance in mind.

## Quick Start

### Prerequisites

* Java 21 (What I used) or Java 17+(Atleast)
* Maven 3.8+
* Docker (Optional, for containerized deployment)

### Clone & Run (Development Mode)

```bash
# Clone the repository
git clone https://github.com/DeveloperWilliams/ohs-backend-developer-exercise
cd ohs-backend-developer-exercise

# Run with detailed logging
mvn clean spring-boot:run -X

# Or run normally
mvn clean spring-boot:run
```

**Access Points (Development)**

* API Base URL: [http://localhost:8080](http://localhost:8080)
* Swagger UI Documentation: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* H2 Database Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
* Health Check: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### Production Deployment

```bash
# Using Docker
docker-compose -f docker/docker-compose.yml up --build

# Access production API
curl https://health.backend.williamachuchi.com/api/patients
```

## Technology Stack

* Java 21 - Runtime environment
* Spring Boot 3.3.4 - Application framework
* H2 Database - In-memory database (Development)
* PostgreSQL - Production database(Not ready H2 used)
* Spring Data JPA - Data access layer
* Spring Security - Authentication & Authorization
* Spring Validation - Input validation
* OpenAPI 3 - API documentation
* Docker - Containerization

## API Features

### Patient Management

* Create, read, update, delete patients
* Search by family name, given name, identifier, birth date
* Advanced search with multiple criteria
* Pagination and sorting support

### Encounter Management

* Record patient visits (INPATIENT, OUTPATIENT, EMERGENCY, VIRTUAL)
* Date range queries and filtering
* Patient encounter history
* Encounter classification

### Data Integrity & Validation

* Comprehensive input validation
* Unique patient identifiers
* Date validation and business rules
* Proper error handling with consistent responses

## Database Configuration

### Development (H2)

```properties
spring.datasource.url=jdbc:h2:mem:healthdb
spring.h2.console.enabled=true
```

### Production (PostgreSQL)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/healthdb
spring.datasource.username=healthuser
spring.datasource.password=healthpass
```

## Configuration

### Key Application Properties

```properties
# Server
server.port=8080

# Security (Development - Disabled for testing)
app.security.enabled=false

# Database
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create-drop

# Date Format
spring.jackson.serialization.write-dates-as-timestamps=false
```

## Testing

### Run All Tests

```bash
mvn test
```

### Test Specific Components

```bash
# Unit tests only
mvn test -Dtest=*ServiceTest,*RepositoryTest

# Integration tests
mvn test -Dtest=*IntegrationTest

# Controller tests
mvn test -Dtest=*ControllerTest
```

### Manual Testing via Swagger

1. Start the application
2. Navigate to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
3. Use the "Try it out" feature for each endpoint
4. Test with sample data provided in the examples

## Troubleshooting

### Common Issues

**Port 8080 Already in Use**

```bash
sudo lsof -i :8080
# Kill the process or use different port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

**Java Version Issues**

```bash
java -version
# Should show Java 17 or 21
```

**Maven Dependencies**

```bash
mvn clean
mvn dependency:purge-local-repository
mvn install
```

**Database Connection**

* H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
* JDBC URL: jdbc:h2:mem:healthdb
* Username: sa
* Password: (empty)

**Verification Steps**

* Application starts without errors
* Swagger UI loads with all endpoints
* Health endpoint returns {"status":"UP"}
* H2 console accessible and shows sample data
* API endpoints respond without authentication

## Security Considerations

### Development Mode

* Spring Security is disabled for easier testing and analysis
* H2 console is enabled for database inspection
* CORS is configured for local development

### Production Readiness

* API Key authentication is implemented but disabled
* Input validation and sanitization are active
* Proper error handling prevents information leakage
* SQL injection protection via JPA/Hibernate

**Security Features (Code-Commented for Analysis)**

```java
// API Key authentication filter
// CORS configuration
// Input validation annotations
// SQL injection prevention
// XSS protection headers
```

## Project Structure

```
src/
├── main/
│   ├── java/com/countyhospital/healthapi/
│   │   ├── patient/           # Patient domain
│   │   ├── encounter/         # Encounter domain
│   │   ├── observation/       # Observation domain
│   │   ├── common/           # Shared components
│   │   └── config/           # Configuration
│   └── resources/
│       ├── application.properties
│       └── db/               # Database scripts
└── test/                     # Comprehensive test suite
```

## Design Principles

### Layered Architecture

* Controller Layer: REST endpoints, input validation
* Service Layer: Business logic, transaction management
* Repository Layer: Data access, JPA operations
* Domain Layer: Business entities, validation rules

### Healthcare Standards

* FHIR-inspired data models
* ISO date formats throughout
* Standardized gender codes (MALE, FEMALE, OTHER, UNKNOWN)
* Encounter classification following healthcare norms

### Production Considerations

* Comprehensive error handling with consistent responses
* Proper logging and monitoring setup
* Database indexing for performance
* Input validation and sanitization
* Security headers and CORS configuration

#BY DEV William Achuchi