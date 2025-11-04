package com.countyhospital.healthapi.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.countyhospital.healthapi.common.exception.BusinessException;
import com.countyhospital.healthapi.common.exception.ResourceNotFoundException;
import com.countyhospital.healthapi.common.exception.ValidationException;
import com.countyhospital.healthapi.encounter.domain.Encounter;
import com.countyhospital.healthapi.encounter.repository.EncounterRepository;
import com.countyhospital.healthapi.encounter.service.EncounterServiceImpl;
import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.service.PatientService;

@ExtendWith(MockitoExtension.class)
class EncounterServiceTest {

    @Mock
    private EncounterRepository encounterRepository;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private EncounterServiceImpl encounterService;

    private Patient patient;
    private Encounter encounter1;
    private Encounter encounter2;

    @BeforeEach
    void setUp() {
        patient = new Patient("PAT-SERVICE-001", "John", "Doe", 
                             java.time.LocalDate.of(1985, 5, 15), "MALE");
        patient.setId(1L);

        encounter1 = new Encounter(patient,
                LocalDateTime.of(2024, 1, 10, 9, 0),
                LocalDateTime.of(2024, 1, 10, 10, 30),
                "OUTPATIENT", "Annual physical examination");
        encounter1.setId(1L);

        encounter2 = new Encounter(patient,
                LocalDateTime.of(2024, 1, 15, 14, 15),
                LocalDateTime.of(2024, 1, 15, 15, 0),
                "OUTPATIENT", "Follow-up consultation");
        encounter2.setId(2L);
    }

    @Test
    void whenCreateValidEncounter_thenEncounterIsSaved() {
        // Given
        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(encounterRepository.existsByPatientAndStartDateTime(patient, encounter1.getStartDateTime()))
                .thenReturn(false);
        when(encounterRepository.save(any(Encounter.class))).thenReturn(encounter1);

        // When
        Encounter created = encounterService.createEncounter(encounter1);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getPatient()).isEqualTo(patient);
        assertThat(created.getEncounterClass()).isEqualTo("OUTPATIENT");
        verify(encounterRepository).save(encounter1);
    }

    @Test
    void whenCreateEncounterWithDuplicateStartTime_thenThrowException() {
        // Given
        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(encounterRepository.existsByPatientAndStartDateTime(patient, encounter1.getStartDateTime()))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> encounterService.createEncounter(encounter1))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Encounter already exists");

        verify(encounterRepository, never()).save(any(Encounter.class));
    }

    @Test
    void whenCreateEncounterWithInvalidDateRange_thenThrowException() {
        // Given
        Encounter invalidEncounter = new Encounter(patient,
                LocalDateTime.of(2024, 1, 10, 9, 0),
                LocalDateTime.of(2024, 1, 10, 8, 0), // End before start
                "OUTPATIENT", "Invalid date range");

        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(encounterRepository.existsByPatientAndStartDateTime(patient, invalidEncounter.getStartDateTime()))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> encounterService.createEncounter(invalidEncounter))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("End date time cannot be before start date time");
    }

    @Test
    void whenGetExistingEncounterById_thenReturnEncounter() {
        // Given
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter1));

        // When
        Encounter found = encounterService.getEncounterById(1L);

        // Then
        assertThat(found).isEqualTo(encounter1);
    }

    @Test
    void whenGetNonExistingEncounterById_thenThrowException() {
        // Given
        when(encounterRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> encounterService.getEncounterById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void whenGetEncountersByPatientId_thenReturnEncounters() {
        // Given
        List<Encounter> encounters = Arrays.asList(encounter1, encounter2);
        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(encounterRepository.findByPatientId(1L)).thenReturn(encounters);

        // When
        List<Encounter> found = encounterService.getEncountersByPatientId(1L);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).containsExactly(encounter1, encounter2);
    }

    @Test
    void whenGetEncountersByPatientIdWithPagination_thenReturnPage() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    when(patientService.getPatientById(1L)).thenReturn(patient);
    // when(encounterRepository.findAll(any(), eq(pageable))).thenReturn(new PageImpl<>(Arrays.asList(encounter1, encounter2)));
    when(encounterRepository.findByPatientId(1L)).thenReturn(Arrays.asList(encounter1, encounter2));

        // When
        Page<Encounter> result = encounterService.getEncountersByPatientId(1L, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void whenGetEncountersByDateRange_thenReturnFilteredEncounters() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 9, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 11, 0, 0);
        List<Encounter> encounters = Arrays.asList(encounter1);
        
        when(encounterRepository.findByStartDateTimeBetween(start, end)).thenReturn(encounters);

        // When
        List<Encounter> found = encounterService.getEncountersByDateRange(start, end);

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0)).isEqualTo(encounter1);
    }

    @Test
    void whenGetEncountersByClass_thenReturnMatchingEncounters() {
        // Given
        List<Encounter> encounters = Arrays.asList(encounter1, encounter2);
        when(encounterRepository.findByEncounterClass("OUTPATIENT")).thenReturn(encounters);

        // When
        List<Encounter> found = encounterService.getEncountersByClass("OUTPATIENT");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Encounter::getEncounterClass)
                .allMatch(classType -> classType.equals("OUTPATIENT"));
    }

    @Test
    void whenGetEncountersByInvalidClass_thenThrowException() {
        // When & Then
        assertThatThrownBy(() -> encounterService.getEncountersByClass("INVALID"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Encounter class must be");
    }

    @Test
    void whenUpdateValidEncounter_thenEncounterIsUpdated() {
        // Given
        Encounter updatedDetails = new Encounter(patient,
                LocalDateTime.of(2024, 1, 10, 9, 0),
                LocalDateTime.of(2024, 1, 10, 11, 0), // Updated end time
                "INPATIENT", "Updated description");

        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter1));
        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(encounterRepository.existsByPatientAndStartDateTime(patient, updatedDetails.getStartDateTime()))
                .thenReturn(false);
        when(encounterRepository.save(any(Encounter.class))).thenReturn(encounter1);

        // When
        Encounter updated = encounterService.updateEncounter(1L, updatedDetails);

        // Then
        assertThat(updated).isNotNull();
        verify(encounterRepository).save(any(Encounter.class));
    }

    @Test
    void whenUpdateEncounterWithDuplicateStartTime_thenThrowException() {
        // Given
        Encounter updatedDetails = new Encounter(patient,
                LocalDateTime.of(2024, 1, 20, 9, 0), // Different start time
                LocalDateTime.of(2024, 1, 20, 10, 0),
                "OUTPATIENT", "Updated");

        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter1));
        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(encounterRepository.existsByPatientAndStartDateTime(patient, updatedDetails.getStartDateTime()))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> encounterService.updateEncounter(1L, updatedDetails))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Another encounter already exists");
    }

    @Test
    void whenDeleteExistingEncounter_thenEncounterIsDeleted() {
        // Given
        when(encounterRepository.findById(1L)).thenReturn(Optional.of(encounter1));
        doNothing().when(encounterRepository).delete(encounter1);

        // When
        encounterService.deleteEncounter(1L);

        // Then
        verify(encounterRepository).delete(encounter1);
    }

    @Test
    void whenGetEncounterCountByPatientId_thenReturnCorrectCount() {
        // Given
        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(encounterRepository.countByPatientId(1L)).thenReturn(2L);

        // When
        long count = encounterService.getEncounterCountByPatientId(1L);

        // Then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    void whenCheckEncounterExists_thenReturnCorrectValue() {
        // Given
        when(encounterRepository.existsById(1L)).thenReturn(true);
        when(encounterRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThat(encounterService.encounterExists(1L)).isTrue();
        assertThat(encounterService.encounterExists(99L)).isFalse();
    }

    @Test
    void whenGetAllEncountersWithPagination_thenReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Encounter> encounterPage = new PageImpl<>(Arrays.asList(encounter1, encounter2));
        when(encounterRepository.findAll(pageable)).thenReturn(encounterPage);

        // When
        Page<Encounter> result = encounterService.getAllEncounters(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }
}