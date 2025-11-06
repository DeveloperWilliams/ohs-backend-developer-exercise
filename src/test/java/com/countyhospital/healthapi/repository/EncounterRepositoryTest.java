package com.countyhospital.healthapi.repository;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.countyhospital.healthapi.encounter.domain.Encounter;
import com.countyhospital.healthapi.encounter.repository.EncounterRepository;
import com.countyhospital.healthapi.patient.domain.Patient;

@DataJpaTest
@ActiveProfiles("test")
class EncounterRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EncounterRepository encounterRepository;

    private Patient patient1;
    private Patient patient2;
    private Encounter encounter1;
    private Encounter encounter2;
    private Encounter encounter3;

    @BeforeEach
    public void setUp() {
        
        entityManager.clear();

        // Create test patients
        patient1 = new Patient("PAT-REPO-001", "John", "Doe", 
                              java.time.LocalDate.of(1985, 5, 15), "MALE");
        patient2 = new Patient("PAT-REPO-002", "Jane", "Smith", 
                              java.time.LocalDate.of(1990, 8, 22), "FEMALE");

        entityManager.persist(patient1);
        entityManager.persist(patient2);
        entityManager.flush();

        // Create test encounters using Instant
        encounter1 = new Encounter(patient1, 
            Instant.parse("2024-01-10T09:00:00Z"),
            Instant.parse("2024-01-10T10:30:00Z"),
            "OUTPATIENT", "Annual physical examination");
        
        encounter2 = new Encounter(patient1,
            Instant.parse("2024-01-15T14:15:00Z"),
            Instant.parse("2024-01-15T15:00:00Z"),
            "OUTPATIENT", "Follow-up consultation");
        
        encounter3 = new Encounter(patient2,
            Instant.parse("2024-01-12T10:30:00Z"),
            Instant.parse("2024-01-12T11:45:00Z"),
            "EMERGENCY", "Emergency room visit");

        entityManager.persist(encounter1);
        entityManager.persist(encounter2);
        entityManager.persist(encounter3);
        entityManager.flush();
    }

    @Test
    void whenFindByPatientId_thenReturnEncountersForThatPatient() {
        // When
        List<Encounter> encounters = encounterRepository.findByPatientId(patient1.getId());

        // Then
        assertThat(encounters).hasSize(2);
        assertThat(encounters).extracting(Encounter::getPatient)
                .allMatch(patient -> patient.getId().equals(patient1.getId()));
    }

    @Test
    void whenFindByEncounterClass_thenReturnMatchingEncounters() {
        // When
        List<Encounter> encounters = encounterRepository.findByEncounterClass("OUTPATIENT");

        // Then
        assertThat(encounters).hasSize(2);
        assertThat(encounters).extracting(Encounter::getEncounterClass)
                .allMatch(classType -> classType.equals("OUTPATIENT"));
    }

    @Test
    void whenFindByStartDateTimeBetween_thenReturnEncountersInDateRange() {
        // Given
        Instant start = Instant.parse("2024-01-14T00:00:00Z");
        Instant end = Instant.parse("2024-01-16T00:00:00Z");

        // When
        List<Encounter> encounters = encounterRepository.findByStartDateTimeBetween(start, end);

        // Then
        assertThat(encounters).hasSize(1);
        assertThat(encounters.get(0).getStartDateTime())
                .isEqualTo(Instant.parse("2024-01-15T14:15:00Z"));
    }

    @Test
    void whenFindByPatientIdAndStartDateTimeBetween_thenReturnFilteredEncounters() {
        // Given
        Instant start = Instant.parse("2024-01-09T00:00:00Z");
        Instant end = Instant.parse("2024-01-13T00:00:00Z");

        // When
        List<Encounter> encounters = encounterRepository.findByPatientIdAndStartDateTimeBetween(
                patient1.getId(), start, end);

        // Then
        assertThat(encounters).hasSize(1);
        assertThat(encounters.get(0).getStartDateTime())
                .isEqualTo(Instant.parse("2024-01-10T09:00:00Z"));
    }

    @Test
    void whenCountByPatientId_thenReturnCorrectCount() {
        // When
        long count = encounterRepository.countByPatientId(patient1.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void whenExistsByPatientAndStartDateTime_thenReturnTrueForExisting() {
        // When
        boolean exists = encounterRepository.existsByPatientAndStartDateTime(
                patient1, Instant.parse("2024-01-10T09:00:00Z"));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByPatientAndStartDateTime_thenReturnFalseForNonExisting() {
        // When
        boolean exists = encounterRepository.existsByPatientAndStartDateTime(
                patient1, Instant.parse("2024-01-20T09:00:00Z"));

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void whenSaveEncounter_thenEncounterIsPersisted() {
        // Given
        Encounter newEncounter = new Encounter(patient2,
                Instant.parse("2024-01-20T13:00:00Z"),
                Instant.parse("2024-01-20T14:00:00Z"),
                "VIRTUAL", "Telemedicine consultation");

        // When
        Encounter saved = encounterRepository.save(newEncounter);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPatient().getId()).isEqualTo(patient2.getId());
        assertThat(saved.getEncounterClass()).isEqualTo("VIRTUAL");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void whenDeleteEncounter_thenEncounterIsRemoved() {
        // When
        encounterRepository.delete(encounter1);
        List<Encounter> remainingEncounters = encounterRepository.findByPatientId(patient1.getId());

        // Then
        assertThat(remainingEncounters).hasSize(1);
        assertThat(remainingEncounters.get(0).getId()).isEqualTo(encounter2.getId());
    }

    @Test
    void whenFindAll_thenReturnAllEncounters() {
        // When
        List<Encounter> allEncounters = encounterRepository.findAll();

        // Then
        assertThat(allEncounters).hasSize(3);
    }

    @Test
    void whenUpdateEncounter_thenChangesArePersisted() {
        // Given
        encounter1.setDescription("Updated description");
        encounter1.setEncounterClass("INPATIENT");

        // When
        Encounter updated = encounterRepository.save(encounter1);

        // Then
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getEncounterClass()).isEqualTo("INPATIENT");
        assertThat(updated.getUpdatedAt()).isAfter(encounter1.getCreatedAt());
    }

    @Test
    void whenFindByPatientIdWithNoEncounters_thenReturnEmptyList() {
        // Given
        Patient newPatient = new Patient("PAT-REPO-003", "Bob", "Johnson", 
                                       java.time.LocalDate.of(1975, 3, 10), "MALE");
        entityManager.persist(newPatient);
        entityManager.flush();

        // When
        List<Encounter> encounters = encounterRepository.findByPatientId(newPatient.getId());

        // Then
        assertThat(encounters).isEmpty();
    }

    @Test
    void whenFindByStartDateTimeBetweenWithNoResults_thenReturnEmptyList() {
        // Given
        Instant start = Instant.parse("2025-01-01T00:00:00Z");
        Instant end = Instant.parse("2025-01-02T00:00:00Z");

        // When
        List<Encounter> encounters = encounterRepository.findByStartDateTimeBetween(start, end);

        // Then
        assertThat(encounters).isEmpty();
    }

    @Test
    void whenFindByEncounterClassWithNoMatches_thenReturnEmptyList() {
        // When
        List<Encounter> encounters = encounterRepository.findByEncounterClass("INPATIENT");

        // Then
        assertThat(encounters).isEmpty();
    }
}