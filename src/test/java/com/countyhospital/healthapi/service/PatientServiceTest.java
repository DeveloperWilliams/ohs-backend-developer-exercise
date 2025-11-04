package com.countyhospital.healthapi.service;

import java.time.LocalDate;
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
import org.springframework.data.jpa.domain.Specification;

import com.countyhospital.healthapi.common.exception.BusinessException;
import com.countyhospital.healthapi.common.exception.ResourceNotFoundException;
import com.countyhospital.healthapi.common.exception.ValidationException;
import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.repository.PatientRepository;
import com.countyhospital.healthapi.patient.service.PatientServiceImpl;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    public void setUp() {
        patient1 = new Patient("PAT-TEST-001", "John", "Doe", 
                              LocalDate.of(1985, 5, 15), "MALE");
        patient1.setId(1L);

        patient2 = new Patient("PAT-TEST-002", "Jane", "Smith", 
                              LocalDate.of(1990, 8, 22), "FEMALE");
        patient2.setId(2L);
    }

    @Test
    void whenCreateValidPatient_thenPatientIsSaved() {
        // Given
        when(patientRepository.existsByIdentifier("PAT-TEST-001")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient1);

        // When
        Patient created = patientService.createPatient(patient1);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getIdentifier()).isEqualTo("PAT-TEST-001");
        verify(patientRepository).save(patient1);
    }

    @Test
    void whenCreatePatientWithExistingIdentifier_thenThrowException() {
        // Given
        when(patientRepository.existsByIdentifier("PAT-TEST-001")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> patientService.createPatient(patient1))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");

        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void whenGetExistingPatientById_thenReturnPatient() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));

        // When
        Patient found = patientService.getPatientById(1L);

        // Then
        assertThat(found).isEqualTo(patient1);
    }

    @Test
    void whenGetNonExistingPatientById_thenThrowException() {
        // Given
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> patientService.getPatientById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void whenUpdateValidPatient_thenPatientIsUpdated() {
        // Given
        Patient updatedDetails = new Patient("PAT-TEST-001-UPDATED", "Johnny", "Doey", 
                                           LocalDate.of(1985, 5, 15), "MALE");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));
        when(patientRepository.existsByIdentifierAndIdNot("PAT-TEST-001-UPDATED", 1L)).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient1);

        // When
        Patient updated = patientService.updatePatient(1L, updatedDetails);

        // Then
        assertThat(updated).isNotNull();
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void whenUpdatePatientWithDuplicateIdentifier_thenThrowException() {
        // Given
        Patient updatedDetails = new Patient("PAT-TEST-DUPLICATE", "John", "Doe", 
                                           LocalDate.of(1985, 5, 15), "MALE");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));
        when(patientRepository.existsByIdentifierAndIdNot("PAT-TEST-DUPLICATE", 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> patientService.updatePatient(1L, updatedDetails))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void whenDeleteExistingPatient_thenPatientIsDeleted() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));
        doNothing().when(patientRepository).delete(patient1);

        // When
        patientService.deletePatient(1L);

        // Then
        verify(patientRepository).delete(patient1);
    }

    @Test
    void whenSearchPatientsWithCriteria_thenReturnPatients() {
        // Given
        List<Patient> patients = Arrays.asList(patient1, patient2);
        when(patientRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Patient>>any())).thenReturn(patients);

        // When
        List<Patient> found = patientService.searchPatients("Doe", "John", null, 
                                                           LocalDate.of(1985, 5, 15), null, null);

        // Then
        assertThat(found).hasSize(2);
    }

    @Test
    void whenSearchWithNoCriteria_thenThrowException() {
        // When & Then
        assertThatThrownBy(() -> patientService.searchPatients(null, null, null, null, null, null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("At least one search criteria");
    }

    @Test
    void whenGetAllPatientsWithPagination_thenReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(Arrays.asList(patient1, patient2));
        when(patientRepository.findAll(pageable)).thenReturn(patientPage);

        // When
        Page<Patient> result = patientService.getAllPatients(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void whenCheckPatientExists_thenReturnCorrectValue() {
        // Given
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(patientRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThat(patientService.patientExists(1L)).isTrue();
        assertThat(patientService.patientExists(99L)).isFalse();
    }
}