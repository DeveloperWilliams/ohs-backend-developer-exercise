package com.countyhospital.healthapi.encounter.dto.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for encounter data")
public class EncounterResponse {

    @Schema(description = "Unique encounter ID", example = "1")
    private Long id;

    @Schema(description = "Patient ID", example = "1")
    private Long patientId;

    @Schema(description = "Patient given name", example = "John")
    private String patientGivenName;

    @Schema(description = "Patient family name", example = "Doe")
    private String patientFamilyName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = "Encounter start date time in UTC", example = "2024-01-15T09:30:00.000Z")
    private Instant startDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = "Encounter end date time in UTC", example = "2024-01-15T10:15:00.000Z")
    private Instant endDateTime;

    @Schema(description = "Type of encounter", example = "OUTPATIENT")
    private String encounterClass;

    @Schema(description = "Encounter description or notes", example = "Routine checkup")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = "Encounter creation timestamp in UTC")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = "Encounter last update timestamp in UTC")
    private Instant updatedAt;

    // Constructors
    public EncounterResponse() {}

    public EncounterResponse(Long id, Long patientId, String patientGivenName, String patientFamilyName,
                            Instant startDateTime, Instant endDateTime, String encounterClass,
                            String description, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.patientGivenName = patientGivenName;
        this.patientFamilyName = patientFamilyName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.encounterClass = encounterClass;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientGivenName() { return patientGivenName; }
    public void setPatientGivenName(String patientGivenName) { this.patientGivenName = patientGivenName; }

    public String getPatientFamilyName() { return patientFamilyName; }
    public void setPatientFamilyName(String patientFamilyName) { this.patientFamilyName = patientFamilyName; }

    public Instant getStartDateTime() { return startDateTime; }
    public void setStartDateTime(Instant startDateTime) { this.startDateTime = startDateTime; }

    public Instant getEndDateTime() { return endDateTime; }
    public void setEndDateTime(Instant endDateTime) { this.endDateTime = endDateTime; }

    public String getEncounterClass() { return encounterClass; }
    public void setEncounterClass(String encounterClass) { this.encounterClass = encounterClass; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "EncounterResponse{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", patientGivenName='" + patientGivenName + '\'' +
                ", patientFamilyName='" + patientFamilyName + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", encounterClass='" + encounterClass + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}