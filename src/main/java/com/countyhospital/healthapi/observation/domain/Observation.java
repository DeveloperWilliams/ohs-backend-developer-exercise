package com.countyhospital.healthapi.observation.domain;

import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.encounter.domain.Encounter;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "observations", indexes = {
    @Index(name = "idx_observation_patient_id", columnList = "patient_id"),
    @Index(name = "idx_observation_encounter_id", columnList = "encounter_id"),
    @Index(name = "idx_observation_effective_date", columnList = "effectiveDateTime"),
    @Index(name = "idx_observation_code", columnList = "code")
})
public class Observation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id")
    private Encounter encounter;

    @NotBlank(message = "Observation code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String code;

    @NotBlank(message = "Display name is required")
    @Size(max = 200, message = "Display name must not exceed 200 characters")
    @Column(name = "display_name", nullable = false, length = 200)
    private String displayName;

    @NotBlank(message = "Value is required")
    @Size(max = 500, message = "Value must not exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String value;

    @Size(max = 50, message = "Unit must not exceed 50 characters")
    @Column(length = 50)
    private String unit;

    @NotNull(message = "Effective date time is required")
    @Column(nullable = false)
    private LocalDateTime effectiveDateTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor for JPA
    public Observation() {}

    // Constructor for creating new observations
    public Observation(Patient patient, Encounter encounter, String code, 
                      String displayName, String value, String unit, LocalDateTime effectiveDateTime) {
        this.patient = patient;
        this.encounter = encounter;
        this.code = code;
        this.displayName = displayName;
        this.value = value;
        this.unit = unit;
        this.effectiveDateTime = effectiveDateTime;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Encounter getEncounter() { return encounter; }
    public void setEncounter(Encounter encounter) { this.encounter = encounter; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDateTime getEffectiveDateTime() { return effectiveDateTime; }
    public void setEffectiveDateTime(LocalDateTime effectiveDateTime) { this.effectiveDateTime = effectiveDateTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Observation that = (Observation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Observation{" +
                "id=" + id +
                ", patientId=" + (patient != null ? patient.getId() : null) +
                ", encounterId=" + (encounter != null ? encounter.getId() : null) +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                ", effectiveDateTime=" + effectiveDateTime +
                '}';
    }
}