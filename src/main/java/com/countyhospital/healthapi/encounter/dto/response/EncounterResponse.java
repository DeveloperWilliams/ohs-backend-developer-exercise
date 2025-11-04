package com.countyhospital.healthapi.encounter.dto.response;

import java.time.LocalDateTime;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Encounter start date time", example = "2024-01-15 09:30:00")
    private LocalDateTime startDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Encounter end date time", example = "2024-01-15 10:15:00")
    private LocalDateTime endDateTime;

    @Schema(description = "Type of encounter", example = "OUTPATIENT")
    private String encounterClass;

    @Schema(description = "Encounter description or notes", example = "Routine checkup")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Encounter creation timestamp")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Encounter last update timestamp")
    private LocalDateTime updatedAt;

    // Constructors
    public EncounterResponse() {}

    public EncounterResponse(Long id, Long patientId, String patientGivenName, String patientFamilyName,
                            LocalDateTime startDateTime, LocalDateTime endDateTime, String encounterClass,
                            String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public String getEncounterClass() { return encounterClass; }
    public void setEncounterClass(String encounterClass) { this.encounterClass = encounterClass; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

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