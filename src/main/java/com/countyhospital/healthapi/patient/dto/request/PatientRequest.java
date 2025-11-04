package com.countyhospital.healthapi.patient.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for creating or updating a patient")
public class PatientRequest {

    @NotBlank(message = "Patient identifier is required")
    @Size(max = 50, message = "Identifier must not exceed 50 characters")
    @Schema(description = "Unique patient identifier", example = "PAT-12345", required = true)
    private String identifier;

    @NotBlank(message = "Given name is required")
    @Size(max = 100, message = "Given name must not exceed 100 characters")
    @Schema(description = "Patient's given name", example = "John", required = true)
    private String givenName;

    @NotBlank(message = "Family name is required")
    @Size(max = 100, message = "Family name must not exceed 100 characters")
    @Schema(description = "Patient's family name", example = "Doe", required = true)
    private String familyName;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Patient's birth date in ISO format (yyyy-MM-dd)", example = "1985-05-15", required = true)
    private LocalDate birthDate;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER|UNKNOWN)$", 
             message = "Gender must be MALE, FEMALE, OTHER, or UNKNOWN")
    @Schema(description = "Patient's gender", example = "MALE", required = true, 
            allowableValues = {"MALE", "FEMALE", "OTHER", "UNKNOWN"})
    private String gender;

    // Default constructor
    public PatientRequest() {}

    // All arguments constructor
    public PatientRequest(String identifier, String givenName, String familyName, 
                         LocalDate birthDate, String gender) {
        this.identifier = identifier;
        this.givenName = givenName;
        this.familyName = familyName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    // Getters and setters
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    @Override
    public String toString() {
        return "PatientRequest{" +
                "identifier='" + identifier + '\'' +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", birthDate=" + birthDate +
                ", gender='" + gender + '\'' +
                '}';
    }
}