package com.countyhospital.healthapi.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.repository.PatientRepository;

@DataJpaTest
@ActiveProfiles("test")
class PatientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    public void setUp() {
        
        entityManager.clear();

        // Create test patients
        patient1 = new Patient("PAT-TEST-001", "John", "Doe", 
                              LocalDate.of(1985, 5, 15), "MALE");
        patient2 = new Patient("PAT-TEST-002", "Jane", "Smith", 
                              LocalDate.of(1990, 8, 22), "FEMALE");

        entityManager.persist(patient1);
        entityManager.persist(patient2);
        entityManager.flush();
    }

    @Test
    void whenFindById_thenReturnPatient() {
        // When
        Optional<Patient> found = patientRepository.findById(patient1.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getIdentifier()).isEqualTo("PAT-TEST-001");
        assertThat(found.get().getGivenName()).isEqualTo("John");
    }

    @Test
    void whenFindByIdentifier_thenReturnPatient() {
        // When
        Optional<Patient> found = patientRepository.findByIdentifier("PAT-TEST-002");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFamilyName()).isEqualTo("Smith");
        assertThat(found.get().getGivenName()).isEqualTo("Jane");
    }

    @Test
    void whenFindByFamilyNameContaining_thenReturnPatients() {
        // When
        List<Patient> patients = patientRepository.findByFamilyNameContainingIgnoreCase("smi");

        // Then
        assertThat(patients).hasSize(1);
        assertThat(patients.get(0).getFamilyName()).isEqualTo("Smith");
    }

    @Test
    void whenFindByGivenNameContaining_thenReturnPatients() {
        // When
        List<Patient> patients = patientRepository.findByGivenNameContainingIgnoreCase("joh");

        // Then
        assertThat(patients).hasSize(1);
        assertThat(patients.get(0).getGivenName()).isEqualTo("John");
    }

    @Test
    void whenFindByBirthDate_thenReturnPatients() {
        // When
        List<Patient> patients = patientRepository.findByBirthDate(LocalDate.of(1985, 5, 15));

        // Then
        assertThat(patients).hasSize(1);
        assertThat(patients.get(0).getIdentifier()).isEqualTo("PAT-TEST-001");
    }

    @Test
    void whenExistsByIdentifier_thenReturnTrue() {
        // When
        boolean exists = patientRepository.existsByIdentifier("PAT-TEST-001");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByIdentifierAndIdNot_thenReturnCorrectValue() {
        // When
        boolean exists = patientRepository.existsByIdentifierAndIdNot("PAT-TEST-001", patient2.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void whenFindAllWithPagination_thenReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Patient> page = patientRepository.findAll(pageable);

        // Then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(1);
    }

    @Test
    void whenSavePatient_thenPatientIsPersisted() {
        // Given
        Patient newPatient = new Patient("PAT-TEST-003", "Bob", "Johnson", 
                                       LocalDate.of(1975, 3, 10), "MALE");

        // When
        Patient saved = patientRepository.save(newPatient);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getIdentifier()).isEqualTo("PAT-TEST-003");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void whenDeletePatient_thenPatientIsRemoved() {
        // When
        patientRepository.delete(patient1);
        Optional<Patient> found = patientRepository.findById(patient1.getId());

        // Then
        assertThat(found).isEmpty();
    }
}