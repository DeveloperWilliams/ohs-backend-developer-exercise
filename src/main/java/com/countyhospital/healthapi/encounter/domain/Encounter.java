package com.countyhospital.healthapi.encounter.domain;

import java.time.Instant;
import java.util.Objects;

import com.countyhospital.healthapi.patient.domain.Patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "encounters", indexes = {
    @Index(name = "idx_encounter_patient_id", columnList = "patient_id"),
    @Index(name = "idx_encounter_start_date", columnList = "startDateTime"),
    @Index(name = "idx_encounter_class", columnList = "encounterClass")
})
public class Encounter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Start date time is required")
    @Column(nullable = false)
    private Instant startDateTime;

    @Column
    private Instant endDateTime;

    @NotBlank(message = "Encounter class is required")
    @Pattern(regexp = "^(INPATIENT|OUTPATIENT|EMERGENCY|VIRTUAL)$", 
             message = "Encounter class must be INPATIENT, OUTPATIENT, EMERGENCY, or VIRTUAL")
    @Column(name = "encounter_class", nullable = false, length = 20)
    private String encounterClass;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Default constructor for JPA
    public Encounter() {}

    // Constructor for creating new encounters
    public Encounter(Patient patient, Instant startDateTime, 
                    Instant endDateTime, String encounterClass, String description) {
        this.patient = patient;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.encounterClass = encounterClass;
        this.description = description;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Business validation
    @AssertTrue(message = "End date time must be after start date time")
    public boolean isValidDateTimeRange() {
        return endDateTime == null || !endDateTime.isBefore(startDateTime);
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

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

    // Equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Encounter encounter = (Encounter) o;
        return Objects.equals(id, encounter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Encounter{" +
                "id=" + id +
                ", patientId=" + (patient != null ? patient.getId() : null) +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", encounterClass='" + encounterClass + '\'' +
                '}';
    }
}