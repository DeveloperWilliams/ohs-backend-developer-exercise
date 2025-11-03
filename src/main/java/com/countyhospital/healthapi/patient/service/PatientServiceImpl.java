package com.countyhospital.healthapi.patient.service;

import com.countyhospital.healthapi.common.exception.BusinessException;
import com.countyhospital.healthapi.common.exception.ResourceNotFoundException;
import com.countyhospital.healthapi.common.exception.ValidationException;
import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.repository.PatientRepository;
import com.countyhospital.healthapi.patient.repository.PatientSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;


@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
    
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Patient createPatient(Patient patient) {
        logger.info("Creating new patient with identifier: {}", patient.getIdentifier());
        
        validatePatientForCreation(patient);
        
        // Check if identifier already exists
        if (patientRepository.existsByIdentifier(patient.getIdentifier())) {
            logger.warn("Patient creation failed: Identifier already exists - {}", patient.getIdentifier());
            throw new BusinessException("Patient with identifier '" + patient.getIdentifier() + "' already exists");
        }
        
        try {
            Patient savedPatient = patientRepository.save(patient);
            logger.info("Successfully created patient with ID: {}", savedPatient.getId());
            return savedPatient;
        } catch (Exception e) {
            logger.error("Failed to create patient: {}", e.getMessage(), e);
            throw new BusinessException("Failed to create patient: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        logger.debug("Retrieving patient by ID: {}", id);
        
        if (id == null) {
            throw new ValidationException("Patient ID cannot be null");
        }
        
        return patientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Patient not found with ID: {}", id);
                    return new ResourceNotFoundException("Patient not found with ID: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Patient getPatientByIdentifier(String identifier) {
        logger.debug("Retrieving patient by identifier: {}", identifier);
        
        if (!StringUtils.hasText(identifier)) {
            throw new ValidationException("Patient identifier cannot be null or empty");
        }
        
        return patientRepository.findByIdentifier(identifier)
                .orElseThrow(() -> {
                    logger.warn("Patient not found with identifier: {}", identifier);
                    return new ResourceNotFoundException("Patient not found with identifier: " + identifier);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Patient> getAllPatients(Pageable pageable) {
        logger.debug("Retrieving all patients with pagination: {}", pageable);
        return patientRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> searchPatients(String familyName, String givenName, String identifier, 
                                      LocalDate birthDate, LocalDate birthDateStart, LocalDate birthDateEnd) {
        logger.debug("Searching patients with criteria - familyName: {}, givenName: {}, identifier: {}, birthDate: {}, range: {} to {}", 
                familyName, givenName, identifier, birthDate, birthDateStart, birthDateEnd);
        
        validateSearchCriteria(familyName, givenName, identifier, birthDate, birthDateStart, birthDateEnd);
        
        Specification<Patient> spec = PatientSpecifications.searchByCriteria(
                familyName, givenName, identifier, birthDate, birthDateStart, birthDateEnd);
        
        return patientRepository.findAll(spec);
    }

    @Override
    public Patient updatePatient(Long id, Patient patientDetails) {
        logger.info("Updating patient with ID: {}", id);
        
        if (id == null) {
            throw new ValidationException("Patient ID cannot be null for update");
        }
        
        validatePatientForUpdate(patientDetails);
        
        Patient existingPatient = getPatientById(id);
        
        // Check if identifier is being changed and if it conflicts with another patient
        if (!existingPatient.getIdentifier().equals(patientDetails.getIdentifier()) &&
            patientRepository.existsByIdentifierAndIdNot(patientDetails.getIdentifier(), id)) {
            logger.warn("Patient update failed: Identifier already exists for another patient - {}", patientDetails.getIdentifier());
            throw new BusinessException("Identifier '" + patientDetails.getIdentifier() + "' already exists for another patient");
        }
        
        // Update patient fields
        updatePatientFields(existingPatient, patientDetails);
        
        try {
            Patient updatedPatient = patientRepository.save(existingPatient);
            logger.info("Successfully updated patient with ID: {}", id);
            return updatedPatient;
        } catch (Exception e) {
            logger.error("Failed to update patient with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException("Failed to update patient: " + e.getMessage());
        }
    }

    @Override
    public void deletePatient(Long id) {
        logger.info("Deleting patient with ID: {}", id);
        
        if (id == null) {
            throw new ValidationException("Patient ID cannot be null for deletion");
        }
        
        Patient patient = getPatientById(id);
        
        try {
            patientRepository.delete(patient);
            logger.info("Successfully deleted patient with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete patient with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException("Failed to delete patient: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean patientExists(Long id) {
        return id != null && patientRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean identifierExists(String identifier) {
        return StringUtils.hasText(identifier) && patientRepository.existsByIdentifier(identifier);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean identifierExistsForOtherPatient(String identifier, Long excludedPatientId) {
        return StringUtils.hasText(identifier) && 
               patientRepository.existsByIdentifierAndIdNot(identifier, excludedPatientId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Patient> searchPatients(Specification<Patient> spec, Pageable pageable) {
        return patientRepository.findAll(spec, pageable);
    }

    // Private helper methods
    private void validatePatientForCreation(Patient patient) {
        if (patient == null) {
            throw new ValidationException("Patient cannot be null");
        }
        
        if (!StringUtils.hasText(patient.getIdentifier())) {
            throw new ValidationException("Patient identifier is required");
        }
        
        if (!StringUtils.hasText(patient.getGivenName())) {
            throw new ValidationException("Patient given name is required");
        }
        
        if (!StringUtils.hasText(patient.getFamilyName())) {
            throw new ValidationException("Patient family name is required");
        }
        
        if (patient.getBirthDate() == null) {
            throw new ValidationException("Patient birth date is required");
        }
        
        if (patient.getBirthDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Patient birth date cannot be in the future");
        }
        
        if (!StringUtils.hasText(patient.getGender())) {
            throw new ValidationException("Patient gender is required");
        }
        
        validateGender(patient.getGender());
    }

    private void validatePatientForUpdate(Patient patient) {
        if (patient == null) {
            throw new ValidationException("Patient details cannot be null for update");
        }
        
        if (!StringUtils.hasText(patient.getIdentifier())) {
            throw new ValidationException("Patient identifier is required");
        }
        
        if (!StringUtils.hasText(patient.getGivenName())) {
            throw new ValidationException("Patient given name is required");
        }
        
        if (!StringUtils.hasText(patient.getFamilyName())) {
            throw new ValidationException("Patient family name is required");
        }
        
        if (patient.getBirthDate() == null) {
            throw new ValidationException("Patient birth date is required");
        }
        
        if (!StringUtils.hasText(patient.getGender())) {
            throw new ValidationException("Patient gender is required");
        }
        
        validateGender(patient.getGender());
    }

    private void validateGender(String gender) {
        if (!gender.matches("^(MALE|FEMALE|OTHER|UNKNOWN)$")) {
            throw new ValidationException("Gender must be MALE, FEMALE, OTHER, or UNKNOWN");
        }
    }

    private void validateSearchCriteria(String familyName, String givenName, String identifier, 
                                      LocalDate birthDate, LocalDate birthDateStart, LocalDate birthDateEnd) {
        // Validate that at least one search criteria is provided
        if (!StringUtils.hasText(familyName) && !StringUtils.hasText(givenName) && 
            !StringUtils.hasText(identifier) && birthDate == null && 
            birthDateStart == null && birthDateEnd == null) {
            throw new ValidationException("At least one search criteria must be provided");
        }
        
        // Validate date range
        if (birthDateStart != null && birthDateEnd != null && birthDateStart.isAfter(birthDateEnd)) {
            throw new ValidationException("Start date cannot be after end date");
        }
    }

    private void updatePatientFields(Patient existingPatient, Patient patientDetails) {
        existingPatient.setIdentifier(patientDetails.getIdentifier());
        existingPatient.setGivenName(patientDetails.getGivenName());
        existingPatient.setFamilyName(patientDetails.getFamilyName());
        existingPatient.setBirthDate(patientDetails.getBirthDate());
        existingPatient.setGender(patientDetails.getGender());
        // Note: updatedAt is automatically set by @PreUpdate
    }
}