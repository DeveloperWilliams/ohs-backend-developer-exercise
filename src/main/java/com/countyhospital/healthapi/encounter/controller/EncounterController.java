package com.countyhospital.healthapi.encounter.controller;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.countyhospital.healthapi.encounter.domain.Encounter;
import com.countyhospital.healthapi.encounter.dto.request.EncounterRequest;
import com.countyhospital.healthapi.encounter.dto.response.EncounterResponse;
import com.countyhospital.healthapi.encounter.mapper.EncounterMapper;
import com.countyhospital.healthapi.encounter.service.EncounterService;
import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.service.PatientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/encounters")
public class EncounterController implements EncounterApi {

    private static final Logger logger = LoggerFactory.getLogger(EncounterController.class);
    
    private final EncounterService encounterService;
    private final PatientService patientService;
    private final EncounterMapper encounterMapper;

    public EncounterController(EncounterService encounterService, PatientService patientService, 
                             EncounterMapper encounterMapper) {
        this.encounterService = encounterService;
        this.patientService = patientService;
        this.encounterMapper = encounterMapper;
    }

    @Override
    public ResponseEntity<EncounterResponse> createEncounter(@Valid @RequestBody EncounterRequest encounterRequest) {
        logger.info("Received request to create encounter for patient ID: {}", encounterRequest.getPatientId());
        
        Patient patient = patientService.getPatientById(encounterRequest.getPatientId());
        Encounter encounter = encounterMapper.toEntity(encounterRequest, patient);
        Encounter createdEncounter = encounterService.createEncounter(encounter);
        EncounterResponse response = encounterMapper.toResponse(createdEncounter);
        
        logger.info("Successfully created encounter with ID: {}", createdEncounter.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<EncounterResponse> getEncounterById(Long id) {
        logger.debug("Received request to get encounter by ID: {}", id);
        
        Encounter encounter = encounterService.getEncounterById(id);
        EncounterResponse response = encounterMapper.toResponse(encounter);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<EncounterResponse>> getEncountersByPatientId(Long patientId) {
        logger.debug("Received request to get encounters for patient ID: {}", patientId);
        
        List<Encounter> encounters = encounterService.getEncountersByPatientId(patientId);
        List<EncounterResponse> response = encounterMapper.toResponseList(encounters);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Page<EncounterResponse>> getEncountersByPatientId(
            Long patientId, int page, int size, String sortBy, String direction) {
        
        logger.debug("Received request to get encounters for patient ID: {} with pagination", patientId);
        
        Sort sort = direction.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Encounter> encounters = encounterService.getEncountersByPatientId(patientId, pageable);
        Page<EncounterResponse> response = encounters.map(encounterMapper::toResponse);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Page<EncounterResponse>> getAllEncounters(
            int page, int size, String sortBy, String direction) {
        
        logger.debug("Received request to get all encounters with pagination");
        
        Sort sort = direction.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Encounter> encounters = encounterService.getAllEncounters(pageable);
        Page<EncounterResponse> response = encounters.map(encounterMapper::toResponse);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<EncounterResponse>> getEncountersByDateRange(
            @RequestParam Instant start, 
            @RequestParam Instant end) {
        
        logger.debug("Received request to get encounters in date range: {} to {}", start, end);
        
        List<Encounter> encounters = encounterService.getEncountersByDateRange(start, end);
        List<EncounterResponse> response = encounterMapper.toResponseList(encounters);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<EncounterResponse>> getEncountersByClass(String encounterClass) {
        logger.debug("Received request to get encounters by class: {}", encounterClass);
        
        List<Encounter> encounters = encounterService.getEncountersByClass(encounterClass);
        List<EncounterResponse> response = encounterMapper.toResponseList(encounters);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<EncounterResponse> updateEncounter(
            Long id, @Valid @RequestBody EncounterRequest encounterRequest) {
        
        logger.info("Received request to update encounter with ID: {}", id);
        
        Patient patient = patientService.getPatientById(encounterRequest.getPatientId());
        Encounter encounterDetails = encounterMapper.toEntity(encounterRequest, patient);
        Encounter updatedEncounter = encounterService.updateEncounter(id, encounterDetails);
        EncounterResponse response = encounterMapper.toResponse(updatedEncounter);
        
        logger.info("Successfully updated encounter with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteEncounter(Long id) {
        logger.info("Received request to delete encounter with ID: {}", id);
        
        encounterService.deleteEncounter(id);
        
        logger.info("Successfully deleted encounter with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Long> getEncounterCountByPatientId(Long patientId) {
        long count = encounterService.getEncounterCountByPatientId(patientId);
        return ResponseEntity.ok(count);
    }
}