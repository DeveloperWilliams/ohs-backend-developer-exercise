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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.dto.request.PatientRequest;
import com.countyhospital.healthapi.patient.dto.response.PatientResponse;
import com.countyhospital.healthapi.patient.mapper.PatientMapper;
import com.countyhospital.healthapi.patient.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
@Tag(name = "01 - Patient Management", description = "APIs for managing patient records")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    public PatientController(PatientService patientService, PatientMapper patientMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new patient", description = "Creates a new patient record with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Patient created successfully", 
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Patient with identifier already exists")
    })
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody PatientRequest patientRequest) {
        logger.info("Received request to create patient with identifier: {}", patientRequest.getIdentifier());
        
        Patient patient = patientMapper.toEntity(patientRequest);
        Patient createdPatient = patientService.createPatient(patient);
        PatientResponse response = patientMapper.toResponse(createdPatient);
        
        logger.info("Successfully created patient with ID: {}", createdPatient.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Retrieves a patient record by its unique ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient found", 
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<PatientResponse> getPatientById(
            @Parameter(description = "Patient ID", example = "1") 
            @PathVariable Long id) {
        logger.debug("Received request to get patient by ID: {}", id);
        
        Patient patient = patientService.getPatientById(id);
        PatientResponse response = patientMapper.toResponse(patient);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/identifier/{identifier}")
    @Operation(summary = "Get patient by identifier", description = "Retrieves a patient record by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient found"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<PatientResponse> getPatientByIdentifier(
            @Parameter(description = "Patient identifier", example = "PAT-12345") 
            @PathVariable String identifier) {
        logger.debug("Received request to get patient by identifier: {}", identifier);
        
        Patient patient = patientService.getPatientByIdentifier(identifier);
        PatientResponse response = patientMapper.toResponse(patient);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieves all patients with pagination support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patients retrieved successfully")
    })
    public ResponseEntity<Page<PatientResponse>> getAllPatients(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "familyName") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {
        logger.debug("Received request to get all patients - page: {}, size: {}", page, size);
        
        Sort sort = direction.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Patient> patients = patientService.getAllPatients(pageable);
        Page<PatientResponse> response = patients.map(patientMapper::toResponse);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search patients", description = "Search patients by various criteria with flexible search options")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search criteria")
    })
    public ResponseEntity<List<PatientResponse>> searchPatients(
            @Parameter(description = "Family name (partial match, case insensitive)") 
            @RequestParam(required = false) String familyName,
            
            @Parameter(description = "Given name (partial match, case insensitive)") 
            @RequestParam(required = false) String givenName,
            
            @Parameter(description = "Exact identifier match") 
            @RequestParam(required = false) String identifier,
            
            @Parameter(description = "Exact birth date (yyyy-MM-dd)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            
            @Parameter(description = "Birth date range start (yyyy-MM-dd)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDateStart,
            
            @Parameter(description = "Birth date range end (yyyy-MM-dd)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDateEnd) {
        
        logger.debug("Received search request - familyName: {}, givenName: {}, identifier: {}, birthDate: {}, range: {} to {}", 
                familyName, givenName, identifier, birthDate, birthDateStart, birthDateEnd);
        
        List<Patient> patients = patientService.searchPatients(
                familyName, givenName, identifier, birthDate, birthDateStart, birthDateEnd);
        List<PatientResponse> response = patientMapper.toResponseList(patients);
        
        logger.debug("Search completed, found {} patients", response.size());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update patient", description = "Updates an existing patient record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "409", description = "Identifier conflict with another patient")
    })
    public ResponseEntity<PatientResponse> updatePatient(
            @Parameter(description = "Patient ID") @PathVariable Long id,
            @Valid @RequestBody PatientRequest patientRequest) {
        logger.info("Received request to update patient with ID: {}", id);
        
        Patient patientDetails = patientMapper.toEntity(patientRequest);
        Patient updatedPatient = patientService.updatePatient(id, patientDetails);
        PatientResponse response = patientMapper.toResponse(updatedPatient);
        
        logger.info("Successfully updated patient with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete patient", description = "Deletes a patient record by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "Patient ID") @PathVariable Long id) {
        logger.info("Received request to delete patient with ID: {}", id);
        
        patientService.deletePatient(id);
        
        logger.info("Successfully deleted patient with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if patient exists", description = "Checks if a patient record exists by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Existence check completed")
    })
    public ResponseEntity<Boolean> patientExists(
            @Parameter(description = "Patient ID") @PathVariable Long id) {
        boolean exists = patientService.patientExists(id);
        return ResponseEntity.ok(exists);
    }
}