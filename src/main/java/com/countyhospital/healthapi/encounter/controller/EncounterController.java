package com.countyhospital.healthapi.encounter.controller;

import java.time.LocalDateTime;
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

import com.countyhospital.healthapi.encounter.domain.Encounter;
import com.countyhospital.healthapi.encounter.dto.request.EncounterRequest;
import com.countyhospital.healthapi.encounter.dto.response.EncounterResponse;
import com.countyhospital.healthapi.encounter.mapper.EncounterMapper;
import com.countyhospital.healthapi.encounter.service.EncounterService;
import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/encounters")
@Tag(name = "02 - Encounter Management", description = "APIs for managing patient encounters")
public class EncounterController {

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

    @PostMapping
    @Operation(summary = "Create a new encounter", description = "Creates a new encounter for a patient")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Encounter created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "409", description = "Duplicate encounter")
    })
    public ResponseEntity<EncounterResponse> createEncounter(
            @Valid @RequestBody EncounterRequest encounterRequest) {
        logger.info("Received request to create encounter for patient ID: {}", encounterRequest.getPatientId());
        
        Patient patient = patientService.getPatientById(encounterRequest.getPatientId());
        Encounter encounter = encounterMapper.toEntity(encounterRequest, patient);
        Encounter createdEncounter = encounterService.createEncounter(encounter);
        EncounterResponse response = encounterMapper.toResponse(createdEncounter);
        
        logger.info("Successfully created encounter with ID: {}", createdEncounter.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get encounter by ID", description = "Retrieves an encounter by its unique ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounter found"),
        @ApiResponse(responseCode = "404", description = "Encounter not found")
    })
    public ResponseEntity<EncounterResponse> getEncounterById(
            @Parameter(description = "Encounter ID") @PathVariable Long id) {
        logger.debug("Received request to get encounter by ID: {}", id);
        
        Encounter encounter = encounterService.getEncounterById(id);
        EncounterResponse response = encounterMapper.toResponse(encounter);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get encounters by patient ID", description = "Retrieves all encounters for a specific patient")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounters retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<List<EncounterResponse>> getEncountersByPatientId(
            @Parameter(description = "Patient ID") @PathVariable Long patientId) {
        logger.debug("Received request to get encounters for patient ID: {}", patientId);
        
        List<Encounter> encounters = encounterService.getEncountersByPatientId(patientId);
        List<EncounterResponse> response = encounterMapper.toResponseList(encounters);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}/page")
    @Operation(summary = "Get encounters by patient ID with pagination", 
               description = "Retrieves encounters for a specific patient with pagination support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounters retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<Page<EncounterResponse>> getEncountersByPatientId(
            @Parameter(description = "Patient ID") @PathVariable Long patientId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "startDateTime") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String direction) {
        
        logger.debug("Received request to get encounters for patient ID: {} with pagination", patientId);
        
        Sort sort = direction.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Encounter> encounters = encounterService.getEncountersByPatientId(patientId, pageable);
        Page<EncounterResponse> response = encounters.map(encounterMapper::toResponse);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all encounters", description = "Retrieves all encounters with pagination support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounters retrieved successfully")
    })
    public ResponseEntity<Page<EncounterResponse>> getAllEncounters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDateTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        logger.debug("Received request to get all encounters with pagination");
        
        Sort sort = direction.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Encounter> encounters = encounterService.getAllEncounters(pageable);
        Page<EncounterResponse> response = encounters.map(encounterMapper::toResponse);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/date-range")
    @Operation(summary = "Search encounters by date range", description = "Search encounters within a specific date range")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<List<EncounterResponse>> getEncountersByDateRange(
            @Parameter(description = "Start date time (yyyy-MM-dd HH:mm:ss)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            
            @Parameter(description = "End date time (yyyy-MM-dd HH:mm:ss)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        
        logger.debug("Received request to get encounters in date range: {} to {}", start, end);
        
        List<Encounter> encounters = encounterService.getEncountersByDateRange(start, end);
        List<EncounterResponse> response = encounterMapper.toResponseList(encounters);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/class/{encounterClass}")
    @Operation(summary = "Get encounters by class", description = "Retrieves encounters by encounter class")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounters retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid encounter class")
    })
    public ResponseEntity<List<EncounterResponse>> getEncountersByClass(
            @Parameter(description = "Encounter class") @PathVariable String encounterClass) {
        
        logger.debug("Received request to get encounters by class: {}", encounterClass);
        
        List<Encounter> encounters = encounterService.getEncountersByClass(encounterClass);
        List<EncounterResponse> response = encounterMapper.toResponseList(encounters);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update encounter", description = "Updates an existing encounter")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounter updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Encounter or patient not found"),
        @ApiResponse(responseCode = "409", description = "Duplicate encounter")
    })
    public ResponseEntity<EncounterResponse> updateEncounter(
            @Parameter(description = "Encounter ID") @PathVariable Long id,
            @Valid @RequestBody EncounterRequest encounterRequest) {
        
        logger.info("Received request to update encounter with ID: {}", id);
        
        Patient patient = patientService.getPatientById(encounterRequest.getPatientId());
        Encounter encounterDetails = encounterMapper.toEntity(encounterRequest, patient);
        Encounter updatedEncounter = encounterService.updateEncounter(id, encounterDetails);
        EncounterResponse response = encounterMapper.toResponse(updatedEncounter);
        
        logger.info("Successfully updated encounter with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete encounter", description = "Deletes an encounter by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Encounter deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Encounter not found")
    })
    public ResponseEntity<Void> deleteEncounter(
            @Parameter(description = "Encounter ID") @PathVariable Long id) {
        
        logger.info("Received request to delete encounter with ID: {}", id);
        
        encounterService.deleteEncounter(id);
        
        logger.info("Successfully deleted encounter with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}/count")
    @Operation(summary = "Get encounter count by patient", 
               description = "Returns the number of encounters for a specific patient")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<Long> getEncounterCountByPatientId(
            @Parameter(description = "Patient ID") @PathVariable Long patientId) {
        
        long count = encounterService.getEncounterCountByPatientId(patientId);
        return ResponseEntity.ok(count);
    }
}