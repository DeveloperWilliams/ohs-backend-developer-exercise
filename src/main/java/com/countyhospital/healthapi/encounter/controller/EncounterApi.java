package com.countyhospital.healthapi.encounter.controller;

import java.time.Instant;
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

import com.countyhospital.healthapi.encounter.dto.request.EncounterRequest;
import com.countyhospital.healthapi.encounter.dto.response.EncounterResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "02 - Encounter Management", description = "APIs for managing patient encounters")
public interface EncounterApi {

    @PostMapping
    @Operation(summary = "Create a new encounter", description = "Creates a new encounter for a patient")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Encounter created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "409", description = "Duplicate encounter")
    })
    ResponseEntity<EncounterResponse> createEncounter(@Valid @RequestBody EncounterRequest encounterRequest);

    @GetMapping("/{id}")
    @Operation(summary = "Get encounter by ID", description = "Retrieves an encounter by its unique ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounter found"),
        @ApiResponse(responseCode = "404", description = "Encounter not found")
    })
    ResponseEntity<EncounterResponse> getEncounterById(
            @Parameter(description = "Encounter ID") @PathVariable Long id);

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get encounters by patient ID", description = "Retrieves all encounters for a specific patient")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounters retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    ResponseEntity<List<EncounterResponse>> getEncountersByPatientId(
            @Parameter(description = "Patient ID") @PathVariable Long patientId);

    @GetMapping("/patient/{patientId}/page")
    @Operation(summary = "Get encounters by patient ID with pagination", 
               description = "Retrieves encounters for a specific patient with pagination support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounters retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    ResponseEntity<Page<EncounterResponse>> getEncountersByPatientId(
            @Parameter(description = "Patient ID") @PathVariable Long patientId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "startDateTime") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String direction);

    @GetMapping
    @Operation(summary = "Get all encounters", description = "Retrieves all encounters with pagination support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounters retrieved successfully")
    })
    ResponseEntity<Page<EncounterResponse>> getAllEncounters(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "startDateTime") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String direction);

    @GetMapping("/search/date-range")
    @Operation(summary = "Search encounters by date range", description = "Search encounters within a specific date range")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    ResponseEntity<List<EncounterResponse>> getEncountersByDateRange(
            @Parameter(description = "Start date time (ISO format)") @RequestParam Instant start,
            @Parameter(description = "End date time (ISO format)") @RequestParam Instant end);

    @GetMapping("/search/class/{encounterClass}")
    @Operation(summary = "Get encounters by class", description = "Retrieves encounters by encounter class")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounters retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid encounter class")
    })
    ResponseEntity<List<EncounterResponse>> getEncountersByClass(
            @Parameter(description = "Encounter class") @PathVariable String encounterClass);

    @PutMapping("/{id}")
    @Operation(summary = "Update encounter", description = "Updates an existing encounter")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encounter updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Encounter or patient not found"),
        @ApiResponse(responseCode = "409", description = "Duplicate encounter")
    })
    ResponseEntity<EncounterResponse> updateEncounter(
            @Parameter(description = "Encounter ID") @PathVariable Long id,
            @Valid @RequestBody EncounterRequest encounterRequest);

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete encounter", description = "Deletes an encounter by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Encounter deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Encounter not found")
    })
    ResponseEntity<Void> deleteEncounter(@Parameter(description = "Encounter ID") @PathVariable Long id);

    @GetMapping("/patient/{patientId}/count")
    @Operation(summary = "Get encounter count by patient", 
               description = "Returns the number of encounters for a specific patient")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    ResponseEntity<Long> getEncounterCountByPatientId(
            @Parameter(description = "Patient ID") @PathVariable Long patientId);
}