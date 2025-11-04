package com.countyhospital.healthapi.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.countyhospital.healthapi.encounter.controller.EncounterController;
import com.countyhospital.healthapi.encounter.domain.Encounter;
import com.countyhospital.healthapi.encounter.dto.request.EncounterRequest;
import com.countyhospital.healthapi.encounter.dto.response.EncounterResponse;
import com.countyhospital.healthapi.encounter.mapper.EncounterMapper;
import com.countyhospital.healthapi.encounter.service.EncounterService;
import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(EncounterController.class)
@ActiveProfiles("test")
class EncounterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EncounterService encounterService;

    @MockBean
    private PatientService patientService;

    @MockBean
    private EncounterMapper encounterMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient patient;
    private Encounter encounter;
    private EncounterResponse encounterResponse;
    private EncounterRequest encounterRequest;

    @BeforeEach
    public void setUp() {
        patient = new Patient("PAT-001", "John", "Doe", 
                             LocalDate.of(1985, 5, 15), "MALE");
        patient.setId(1L);
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());

        encounter = new Encounter(patient,
                LocalDateTime.of(2024, 1, 10, 9, 0),
                LocalDateTime.of(2024, 1, 10, 10, 30),
                "OUTPATIENT", "Annual physical examination");
        encounter.setId(1L);
        encounter.setCreatedAt(LocalDateTime.now());
        encounter.setUpdatedAt(LocalDateTime.now());

        encounterResponse = new EncounterResponse(1L, 1L, "John", "Doe",
                LocalDateTime.of(2024, 1, 10, 9, 0),
                LocalDateTime.of(2024, 1, 10, 10, 30),
                "OUTPATIENT", "Annual physical examination",
                encounter.getCreatedAt(), encounter.getUpdatedAt());

        encounterRequest = new EncounterRequest(1L,
                LocalDateTime.of(2024, 1, 10, 9, 0),
                LocalDateTime.of(2024, 1, 10, 10, 30),
                "OUTPATIENT", "Annual physical examination");
    }

    @Test
    void createEncounter_WithValidData_ReturnsCreated() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(encounterMapper.toEntity(any(EncounterRequest.class), eq(patient))).thenReturn(encounter);
        when(encounterService.createEncounter(any(Encounter.class))).thenReturn(encounter);
        when(encounterMapper.toResponse(any(Encounter.class))).thenReturn(encounterResponse);

        mockMvc.perform(post("/api/encounters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encounterRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void createEncounter_WithInvalidData_ReturnsBadRequest() throws Exception {
        EncounterRequest invalidRequest = new EncounterRequest(null, null, null, "", null);

        mockMvc.perform(post("/api/encounters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEncounter_WithValidId_ReturnsEncounter() throws Exception {
        when(encounterService.getEncounterById(1L)).thenReturn(encounter);
        when(encounterMapper.toResponse(encounter)).thenReturn(encounterResponse);

        mockMvc.perform(get("/api/encounters/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getEncounter_WithInvalidId_ReturnsNotFound() throws Exception {
        when(encounterService.getEncounterById(99L))
                .thenThrow(new RuntimeException("Encounter not found"));

        mockMvc.perform(get("/api/encounters/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEncountersByPatientId_ReturnsEncountersList() throws Exception {
        List<Encounter> encounters = Arrays.asList(encounter);
        List<EncounterResponse> responses = Arrays.asList(encounterResponse);

        when(encounterService.getEncountersByPatientId(1L)).thenReturn(encounters);
        when(encounterMapper.toResponseList(encounters)).thenReturn(responses);

        mockMvc.perform(get("/api/encounters/patient/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getEncountersByPatientIdWithPagination_ReturnsPaginatedResults() throws Exception {
        Page<Encounter> encounterPage = new PageImpl<>(Arrays.asList(encounter));
        when(encounterService.getEncountersByPatientId(eq(1L), any(PageRequest.class))).thenReturn(encounterPage);
        when(encounterMapper.toResponse(any(Encounter.class))).thenReturn(encounterResponse);

        mockMvc.perform(get("/api/encounters/patient/1/page")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "startDateTime")
                .param("direction", "desc"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllEncounters_ReturnsPaginatedResults() throws Exception {
        Page<Encounter> encounterPage = new PageImpl<>(Arrays.asList(encounter));
        when(encounterService.getAllEncounters(any(PageRequest.class))).thenReturn(encounterPage);
        when(encounterMapper.toResponse(any(Encounter.class))).thenReturn(encounterResponse);

        mockMvc.perform(get("/api/encounters")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "startDateTime")
                .param("direction", "desc"))
                .andExpect(status().isOk());
    }

    @Test
    void getEncountersByDateRange_ReturnsResults() throws Exception {
        List<Encounter> encounters = Arrays.asList(encounter);
        List<EncounterResponse> responses = Arrays.asList(encounterResponse);

        when(encounterService.getEncountersByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(encounters);
        when(encounterMapper.toResponseList(encounters)).thenReturn(responses);

        mockMvc.perform(get("/api/encounters/search/date-range")
                .param("start", "2024-01-01 00:00:00")
                .param("end", "2024-01-31 23:59:59"))
                .andExpect(status().isOk());
    }

    @Test
    void getEncountersByClass_ReturnsResults() throws Exception {
        List<Encounter> encounters = Arrays.asList(encounter);
        List<EncounterResponse> responses = Arrays.asList(encounterResponse);

        when(encounterService.getEncountersByClass("OUTPATIENT")).thenReturn(encounters);
        when(encounterMapper.toResponseList(encounters)).thenReturn(responses);

        mockMvc.perform(get("/api/encounters/search/class/OUTPATIENT"))
                .andExpect(status().isOk());
    }

    @Test
    void updateEncounter_WithValidData_ReturnsUpdatedEncounter() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(encounterMapper.toEntity(any(EncounterRequest.class), eq(patient))).thenReturn(encounter);
        when(encounterService.updateEncounter(eq(1L), any(Encounter.class))).thenReturn(encounter);
        when(encounterMapper.toResponse(any(Encounter.class))).thenReturn(encounterResponse);

        mockMvc.perform(put("/api/encounters/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encounterRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEncounter_WithValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/encounters/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getEncounterCountByPatient_ReturnsCount() throws Exception {
        when(encounterService.getEncounterCountByPatientId(1L)).thenReturn(3L);

        mockMvc.perform(get("/api/encounters/patient/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void createEncounter_WithNonExistentPatient_ReturnsNotFound() throws Exception {
        when(patientService.getPatientById(99L))
                .thenThrow(new RuntimeException("Patient not found"));

        mockMvc.perform(post("/api/encounters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encounterRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateEncounter_WithInvalidEncounterClass_ReturnsBadRequest() throws Exception {
        EncounterRequest invalidRequest = new EncounterRequest(1L,
                LocalDateTime.of(2024, 1, 10, 9, 0),
                LocalDateTime.of(2024, 1, 10, 10, 30),
                "INVALID_CLASS", "Test description");

        mockMvc.perform(put("/api/encounters/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEncountersByDateRange_WithInvalidDateRange_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/encounters/search/date-range")
                .param("start", "invalid-date")
                .param("end", "2024-01-31 23:59:59"))
                .andExpect(status().isBadRequest());
    }
}