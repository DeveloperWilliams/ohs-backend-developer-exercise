package com.countyhospital.healthapi.encounter.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for creating or updating an encounter")
public class EncounterRequest {

    @NotNull(message = "Patient ID is required")
    @Schema(description = "ID of the patient for this encounter", example = "1", required = true)
    private Long patientId;

    @NotNull(message = "Start date time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Encounter start date time", example = "2024-01-15 09:30:00", required = true)
    private LocalDateTime startDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Encounter end date time", example = "2024-01-15 10:15:00")
    private LocalDateTime endDateTime;

    @NotBlank(message = "Encounter class is required")
    @Pattern(regexp = "^(INPATIENT|OUTPATIENT|EMERGENCY|VIRTUAL)$", 
             message = "Encounter class must be INPATIENT, OUTPATIENT, EMERGENCY, or VIRTUAL")
    @Schema(description = "Type of encounter", example = "OUTPATIENT", required = true,
            allowableValues = {"INPATIENT", "OUTPATIENT", "EMERGENCY", "VIRTUAL"})
    private String encounterClass;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Encounter description or notes", example = "Routine checkup")
    private String description;

    // Constructors
    public EncounterRequest() {}

    public EncounterRequest(Long patientId, LocalDateTime startDateTime, LocalDateTime endDateTime, 
                           String encounterClass, String description) {
        this.patientId = patientId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.encounterClass = encounterClass;
        this.description = description;
    }

    // Getters and setters
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public String getEncounterClass() { return encounterClass; }
    public void setEncounterClass(String encounterClass) { this.encounterClass = encounterClass; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "EncounterRequest{" +
                "patientId=" + patientId +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", encounterClass='" + encounterClass + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}