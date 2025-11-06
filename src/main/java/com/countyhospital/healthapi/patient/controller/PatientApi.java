package com.countyhospital.healthapi.patient.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.countyhospital.healthapi.patient.dto.request.PatientRequest;
import com.countyhospital.healthapi.patient.dto.response.PatientResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "01 - Patient Management", description = "APIs for managing patient records")
public interface PatientApi {

    @PostMapping
    @Operation(summary = "Create a new patient", description = "Creates a new patient record with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Patient created successfully", 
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Patient with identifier already exists")
    })
    ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest patientRequest);

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Retrieves a patient record by its unique ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient found", 
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    ResponseEntity<PatientResponse> getPatientById(
            @Parameter(description = "Patient ID", example = "1") @PathVariable Long id);

    @GetMapping("/identifier/{identifier}")
    @Operation(summary = "Get patient by identifier", description = "Retrieves a patient record by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient found"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    ResponseEntity<PatientResponse> getPatientByIdentifier(
            @Parameter(description = "Patient identifier", example = "PAT-12345") @PathVariable String identifier);

    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieves all patients with pagination support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patients retrieved successfully")
    })
    ResponseEntity<Page<PatientResponse>> getAllPatients(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "familyName") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction);

    @GetMapping("/search")
    @Operation(summary = "Search patients", description = "Search patients by various criteria with flexible search options")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search criteria")
    })
    ResponseEntity<List<PatientResponse>> searchPatients(
            @Parameter(description = "Family name (partial match, case insensitive)") 
            @RequestParam(required = false) String familyName,
            
            @Parameter(description = "Given name (partial match, case insensitive)") 
            @RequestParam(required = false) String givenName,
            
            @Parameter(description = "Exact identifier match") 
            @RequestParam(required = false) String identifier,
            
            @Parameter(description = "Exact birth date (yyyy-MM-dd)") 
            @RequestParam(required = false) LocalDate birthDate,
            
            @Parameter(description = "Birth date range start (yyyy-MM-dd)") 
            @RequestParam(required = false) LocalDate birthDateStart,
            
            @Parameter(description = "Birth date range end (yyyy-MM-dd)") 
            @RequestParam(required = false) LocalDate birthDateEnd);

    @PutMapping("/{id}")
    @Operation(summary = "Update patient", description = "Updates an existing patient record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "409", description = "Identifier conflict with another patient")
    })
    ResponseEntity<PatientResponse> updatePatient(
            @Parameter(description = "Patient ID") @PathVariable Long id,
            @Valid @RequestBody PatientRequest patientRequest);

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete patient", description = "Deletes a patient record by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    ResponseEntity<Void> deletePatient(@Parameter(description = "Patient ID") @PathVariable Long id);

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if patient exists", description = "Checks if a patient record exists by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Existence check completed")
    })
    ResponseEntity<Boolean> patientExists(@Parameter(description = "Patient ID") @PathVariable Long id);
}