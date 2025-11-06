package com.countyhospital.healthapi.patient.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.dto.request.PatientRequest;
import com.countyhospital.healthapi.patient.dto.response.PatientResponse;
import com.countyhospital.healthapi.patient.mapper.PatientMapper;
import com.countyhospital.healthapi.patient.service.PatientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
public class PatientController implements PatientApi {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    public PatientController(PatientService patientService, PatientMapper patientMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    @Override
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest patientRequest) {
        logger.info("Received request to create patient with identifier: {}", patientRequest.getIdentifier());
        
        Patient patient = patientMapper.toEntity(patientRequest);
        Patient createdPatient = patientService.createPatient(patient);
        PatientResponse response = patientMapper.toResponse(createdPatient);
        
        logger.info("Successfully created patient with ID: {}", createdPatient.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        logger.debug("Received request to get patient by ID: {}", id);
        
        Patient patient = patientService.getPatientById(id);
        PatientResponse response = patientMapper.toResponse(patient);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PatientResponse> getPatientByIdentifier(@PathVariable String identifier) {
        logger.debug("Received request to get patient by identifier: {}", identifier);
        
        Patient patient = patientService.getPatientByIdentifier(identifier);
        PatientResponse response = patientMapper.toResponse(patient);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Page<PatientResponse>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "familyName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        logger.debug("Received request to get all patients - page: {}, size: {}", page, size);
        
        Sort sort = direction.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Patient> patients = patientService.getAllPatients(pageable);
        Page<PatientResponse> response = patients.map(patientMapper::toResponse);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<PatientResponse>> searchPatients(
            @RequestParam(required = false) String familyName,
            @RequestParam(required = false) String givenName,
            @RequestParam(required = false) String identifier,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDateEnd) {
        
        logger.debug("Received search request - familyName: {}, givenName: {}, identifier: {}, birthDate: {}, range: {} to {}", 
                familyName, givenName, identifier, birthDate, birthDateStart, birthDateEnd);
        
        List<Patient> patients = patientService.searchPatients(
                familyName, givenName, identifier, birthDate, birthDateStart, birthDateEnd);
        List<PatientResponse> response = patientMapper.toResponseList(patients);
        
        logger.debug("Search completed, found {} patients", response.size());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest patientRequest) {
        
        logger.info("Received request to update patient with ID: {}", id);
        
        Patient patientDetails = patientMapper.toEntity(patientRequest);
        Patient updatedPatient = patientService.updatePatient(id, patientDetails);
        PatientResponse response = patientMapper.toResponse(updatedPatient);
        
        logger.info("Successfully updated patient with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        logger.info("Received request to delete patient with ID: {}", id);
        
        patientService.deletePatient(id);
        
        logger.info("Successfully deleted patient with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Boolean> patientExists(@PathVariable Long id) {
        boolean exists = patientService.patientExists(id);
        return ResponseEntity.ok(exists);
    }
}