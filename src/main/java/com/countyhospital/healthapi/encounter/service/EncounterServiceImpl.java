package com.countyhospital.healthapi.encounter.service;

import com.countyhospital.healthapi.common.exception.BusinessException;
import com.countyhospital.healthapi.common.exception.ResourceNotFoundException;
import com.countyhospital.healthapi.common.exception.ValidationException;
import com.countyhospital.healthapi.encounter.domain.Encounter;
import com.countyhospital.healthapi.encounter.repository.EncounterRepository;
import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class EncounterServiceImpl implements EncounterService {

    private static final Logger logger = LoggerFactory.getLogger(EncounterServiceImpl.class);
    
    private final EncounterRepository encounterRepository;
    private final PatientService patientService;

    public EncounterServiceImpl(EncounterRepository encounterRepository, PatientService patientService) {
        this.encounterRepository = encounterRepository;
        this.patientService = patientService;
    }

    @Override
    public Encounter createEncounter(Encounter encounter) {
        logger.info("Creating new encounter for patient ID: {}", 
                   encounter.getPatient() != null ? encounter.getPatient().getId() : "null");
        
        validateEncounterForCreation(encounter);
        
        // Verify patient exists
        Patient patient = patientService.getPatientById(encounter.getPatient().getId());
        encounter.setPatient(patient);
        
        // Check for duplicate encounters (same patient and start time)
        if (encounterRepository.existsByPatientAndStartDateTime(patient, encounter.getStartDateTime())) {
            logger.warn("Encounter creation failed: Duplicate encounter for patient {} at {}", 
                       patient.getId(), encounter.getStartDateTime());
            throw new BusinessException("Encounter already exists for this patient at the specified start time");
        }
        
        // Validate date logic
        if (encounter.getEndDateTime() != null && 
            encounter.getEndDateTime().isBefore(encounter.getStartDateTime())) {
            throw new ValidationException("End date time cannot be before start date time");
        }
        
        try {
            Encounter savedEncounter = encounterRepository.save(encounter);
            logger.info("Successfully created encounter with ID: {}", savedEncounter.getId());
            return savedEncounter;
        } catch (Exception e) {
            logger.error("Failed to create encounter: {}", e.getMessage(), e);
            throw new BusinessException("Failed to create encounter: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Encounter getEncounterById(Long id) {
        logger.debug("Retrieving encounter by ID: {}", id);
        
        if (id == null) {
            throw new ValidationException("Encounter ID cannot be null");
        }
        
        return encounterRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Encounter not found with ID: {}", id);
                    return new ResourceNotFoundException("Encounter not found with ID: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encounter> getEncountersByPatientId(Long patientId) {
        logger.debug("Retrieving encounters for patient ID: {}", patientId);
        
        validatePatientId(patientId);
        
        // Verify patient exists
        patientService.getPatientById(patientId);
        
        return encounterRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Encounter> getEncountersByPatientId(Long patientId, Pageable pageable) {
        logger.debug("Retrieving encounters for patient ID: {} with pagination: {}", patientId, pageable);
        
        validatePatientId(patientId);
        
        // Verify patient exists
        patientService.getPatientById(patientId);
        
        return encounterRepository.findByPatientId(patientId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encounter> getAllEncounters() {
        logger.debug("Retrieving all encounters");
        return encounterRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Encounter> getAllEncounters(Pageable pageable) {
        logger.debug("Retrieving all encounters with pagination: {}", pageable);
        return encounterRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encounter> getEncountersByDateRange(Instant start, Instant end) {
        logger.debug("Retrieving encounters in date range: {} to {}", start, end);
        
        validateDateRange(start, end);
        
        return encounterRepository.findByStartDateTimeBetween(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encounter> getEncountersByPatientIdAndDateRange(Long patientId, Instant start, Instant end) {
        logger.debug("Retrieving encounters for patient {} in date range: {} to {}", patientId, start, end);
        
        validatePatientId(patientId);
        validateDateRange(start, end);
        
        // Verify patient exists
        patientService.getPatientById(patientId);
        
        return encounterRepository.findByPatientIdAndStartDateTimeBetween(patientId, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encounter> getEncountersByClass(String encounterClass) {
        logger.debug("Retrieving encounters by class: {}", encounterClass);
        
        if (!StringUtils.hasText(encounterClass)) {
            throw new ValidationException("Encounter class cannot be null or empty");
        }
        
        validateEncounterClass(encounterClass);
        
        return encounterRepository.findByEncounterClass(encounterClass);
    }

    @Override
    public Encounter updateEncounter(Long id, Encounter encounterDetails) {
        logger.info("Updating encounter with ID: {}", id);
        
        if (id == null) {
            throw new ValidationException("Encounter ID cannot be null for update");
        }
        
        validateEncounterForUpdate(encounterDetails);
        
        Encounter existingEncounter = getEncounterById(id);
        
        // If patient is being changed, verify new patient exists
        if (!existingEncounter.getPatient().getId().equals(encounterDetails.getPatient().getId())) {
            Patient newPatient = patientService.getPatientById(encounterDetails.getPatient().getId());
            existingEncounter.setPatient(newPatient);
        }
        
        // Check for duplicate if start time is being changed
        if (!existingEncounter.getStartDateTime().equals(encounterDetails.getStartDateTime())) {
            if (encounterRepository.existsByPatientAndStartDateTime(
                    existingEncounter.getPatient(), encounterDetails.getStartDateTime())) {
                logger.warn("Encounter update failed: Duplicate encounter for patient {} at {}", 
                           existingEncounter.getPatient().getId(), encounterDetails.getStartDateTime());
                throw new BusinessException("Another encounter already exists for this patient at the specified start time");
            }
        }
        
        // Validate date logic
        if (encounterDetails.getEndDateTime() != null && 
            encounterDetails.getEndDateTime().isBefore(encounterDetails.getStartDateTime())) {
            throw new ValidationException("End date time cannot be before start date time");
        }
        
        // Update encounter fields
        updateEncounterFields(existingEncounter, encounterDetails);
        
        try {
            Encounter updatedEncounter = encounterRepository.save(existingEncounter);
            logger.info("Successfully updated encounter with ID: {}", id);
            return updatedEncounter;
        } catch (Exception e) {
            logger.error("Failed to update encounter with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException("Failed to update encounter: " + e.getMessage());
        }
    }

    @Override
    public void deleteEncounter(Long id) {
        logger.info("Deleting encounter with ID: {}", id);
        
        if (id == null) {
            throw new ValidationException("Encounter ID cannot be null for deletion");
        }
        
        Encounter encounter = getEncounterById(id);
        
        try {
            encounterRepository.delete(encounter);
            logger.info("Successfully deleted encounter with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete encounter with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException("Failed to delete encounter: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean encounterExists(Long id) {
        return id != null && encounterRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getEncounterCountByPatientId(Long patientId) {
        validatePatientId(patientId);
        return encounterRepository.countByPatientId(patientId);
    }

    // Private helper methods
    private void validateEncounterForCreation(Encounter encounter) {
        if (encounter == null) {
            throw new ValidationException("Encounter cannot be null");
        }
        
        if (encounter.getPatient() == null || encounter.getPatient().getId() == null) {
            throw new ValidationException("Patient is required for encounter creation");
        }
        
        if (encounter.getStartDateTime() == null) {
            throw new ValidationException("Start date time is required");
        }
        
        if (!StringUtils.hasText(encounter.getEncounterClass())) {
            throw new ValidationException("Encounter class is required");
        }
        
        validateEncounterClass(encounter.getEncounterClass());
    }

    private void validateEncounterForUpdate(Encounter encounter) {
        if (encounter == null) {
            throw new ValidationException("Encounter details cannot be null for update");
        }
        
        if (encounter.getPatient() == null || encounter.getPatient().getId() == null) {
            throw new ValidationException("Patient is required");
        }
        
        if (encounter.getStartDateTime() == null) {
            throw new ValidationException("Start date time is required");
        }
        
        if (!StringUtils.hasText(encounter.getEncounterClass())) {
            throw new ValidationException("Encounter class is required");
        }
        
        validateEncounterClass(encounter.getEncounterClass());
    }

    private void validateEncounterClass(String encounterClass) {
        if (!encounterClass.matches("^(INPATIENT|OUTPATIENT|EMERGENCY|VIRTUAL)$")) {
            throw new ValidationException("Encounter class must be INPATIENT, OUTPATIENT, EMERGENCY, or VIRTUAL");
        }
    }

    private void validatePatientId(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("Patient ID cannot be null");
        }
    }

    private void validateDateRange(Instant start, Instant end) {
        if (start == null && end == null) {
            throw new ValidationException("At least one of start or end date must be provided");
        }
        
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Start date cannot be after end date");
        }
    }

    private void updateEncounterFields(Encounter existingEncounter, Encounter encounterDetails) {
        existingEncounter.setPatient(encounterDetails.getPatient());
        existingEncounter.setStartDateTime(encounterDetails.getStartDateTime());
        existingEncounter.setEndDateTime(encounterDetails.getEndDateTime());
        existingEncounter.setEncounterClass(encounterDetails.getEncounterClass());
        existingEncounter.setDescription(encounterDetails.getDescription());
        // Note: updatedAt is automatically set by @PreUpdate
    }
}