package com.countyhospital.healthapi.repository;

import java.time.LocalDateTime;
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

        // Create test encounters
        encounter1 = new Encounter(patient1, 
            LocalDateTime.of(2024, 1, 10, 9, 0),
            LocalDateTime.of(2024, 1, 10, 10, 30),
            "OUTPATIENT", "Annual physical examination");
        
        encounter2 = new Encounter(patient1,
            LocalDateTime.of(2024, 1, 15, 14, 15),
            LocalDateTime.of(2024, 1, 15, 15, 0),
            "OUTPATIENT", "Follow-up consultation");
        
        encounter3 = new Encounter(patient2,
            LocalDateTime.of(2024, 1, 12, 10, 30),
            LocalDateTime.of(2024, 1, 12, 11, 45),
            "EMERGENCY", "Emergency room visit");

        entityManager.persist(encounter1);
        entityManager.persist(encounter2);
        entityManager.persist(encounter3);
        entityManager.flush();
    }

    @Test
    void whenFindByPatient_thenReturnEncountersForThatPatient() {
        // When
        List<Encounter> encounters = encounterRepository.findByPatient(patient1);

        // Then
        assertThat(encounters).hasSize(2);
        assertThat(encounters).extracting(Encounter::getPatient)
                .allMatch(patient -> patient.getId().equals(patient1.getId()));
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
        LocalDateTime start = LocalDateTime.of(2024, 1, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 16, 0, 0);

        // When
        List<Encounter> encounters = encounterRepository.findByStartDateTimeBetween(start, end);

        // Then
        assertThat(encounters).hasSize(1);
        assertThat(encounters.get(0).getStartDateTime())
                .isEqualTo(LocalDateTime.of(2024, 1, 15, 14, 15));
    }

    @Test
    void whenFindByPatientIdAndDateRange_thenReturnFilteredEncounters() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 9, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 13, 0, 0);

        // When
        List<Encounter> encounters = encounterRepository.findByPatientIdAndDateRange(
                patient1.getId(), start, end);

        // Then
        assertThat(encounters).hasSize(1);
        assertThat(encounters.get(0).getStartDateTime())
                .isEqualTo(LocalDateTime.of(2024, 1, 10, 9, 0));
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
                patient1, LocalDateTime.of(2024, 1, 10, 9, 0));

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByPatientAndStartDateTime_thenReturnFalseForNonExisting() {
        // When
        boolean exists = encounterRepository.existsByPatientAndStartDateTime(
                patient1, LocalDateTime.of(2024, 1, 20, 9, 0));

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void whenSaveEncounter_thenEncounterIsPersisted() {
        // Given
        Encounter newEncounter = new Encounter(patient2,
                LocalDateTime.of(2024, 1, 20, 13, 0),
                LocalDateTime.of(2024, 1, 20, 14, 0),
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
        List<Encounter> remainingEncounters = encounterRepository.findByPatient(patient1);

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
}