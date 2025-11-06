package com.countyhospital.healthapi.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.countyhospital.healthapi.patient.controller.PatientController;
import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.dto.request.PatientRequest;
import com.countyhospital.healthapi.patient.dto.response.PatientResponse;
import com.countyhospital.healthapi.patient.mapper.PatientMapper;
import com.countyhospital.healthapi.patient.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PatientService patientService;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientController patientController;

    private ObjectMapper objectMapper;

    private Patient patient;
    private PatientResponse patientResponse;
    private PatientRequest patientRequest;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        
        // Setup MockMvc standalone
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        patient = new Patient("PAT-001", "John", "Doe", 
                             LocalDate.of(1985, 5, 15), "MALE");
        patient.setId(1L);
        patient.setCreatedAt(Instant.now());
        patient.setUpdatedAt(Instant.now());

        patientResponse = new PatientResponse(1L, "PAT-001", "John", "Doe", 
                                            LocalDate.of(1985, 5, 15), "MALE",
                                            patient.getCreatedAt(), patient.getUpdatedAt());

        patientRequest = new PatientRequest("PAT-001", "John", "Doe", 
                                          LocalDate.of(1985, 5, 15), "MALE");
    }

    @Test
    void createPatient_WithValidData_ReturnsCreated() throws Exception {
        when(patientMapper.toEntity(any(PatientRequest.class))).thenReturn(patient);
        when(patientService.createPatient(any(Patient.class))).thenReturn(patient);
        when(patientMapper.toResponse(any(Patient.class))).thenReturn(patientResponse);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void createPatient_WithInvalidData_ReturnsBadRequest() throws Exception {
        PatientRequest invalidRequest = new PatientRequest("", "", "", null, "");

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPatient_WithValidId_ReturnsPatient() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(patientMapper.toResponse(any(Patient.class))).thenReturn(patientResponse);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPatient_WithInvalidId_ReturnsNotFound() throws Exception {
        when(patientService.getPatientById(99L))
                .thenThrow(new RuntimeException("Patient not found"));

        mockMvc.perform(get("/api/patients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPatients_ReturnsPaginatedResults() throws Exception {
        Page<Patient> patientPage = new PageImpl<>(Arrays.asList(patient));
        when(patientService.getAllPatients(any(PageRequest.class))).thenReturn(patientPage);
        when(patientMapper.toResponse(any(Patient.class))).thenReturn(patientResponse);

        mockMvc.perform(get("/api/patients")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "familyName")
                .param("direction", "asc"))
                .andExpect(status().isOk());
    }

    @Test
    void searchPatients_WithCriteria_ReturnsResults() throws Exception {
        List<Patient> patients = Arrays.asList(patient);
        List<PatientResponse> responses = Arrays.asList(patientResponse);
        
        when(patientService.searchPatients(anyString(), anyString(), anyString(), 
                                          any(), any(), any())).thenReturn(patients);
        when(patientMapper.toResponseList(patients)).thenReturn(responses);

        mockMvc.perform(get("/api/patients/search")
                .param("familyName", "Doe")
                .param("givenName", "John"))
                .andExpect(status().isOk());
    }

    @Test
    void updatePatient_WithValidData_ReturnsUpdatedPatient() throws Exception {
        when(patientMapper.toEntity(any(PatientRequest.class))).thenReturn(patient);
        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(patient);
        when(patientMapper.toResponse(any(Patient.class))).thenReturn(patientResponse);

        mockMvc.perform(put("/api/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deletePatient_WithValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void checkPatientExists_ReturnsTrue() throws Exception {
        when(patientService.patientExists(1L)).thenReturn(true);

        mockMvc.perform(get("/api/patients/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getPatientByIdentifier_WithValidIdentifier_ReturnsPatient() throws Exception {
        when(patientService.getPatientByIdentifier("PAT-001")).thenReturn(patient);
        when(patientMapper.toResponse(any(Patient.class))).thenReturn(patientResponse);

        mockMvc.perform(get("/api/patients/identifier/PAT-001"))
                .andExpect(status().isOk());
    }

    @Test
    void createPatient_WithDuplicateIdentifier_ReturnsConflict() throws Exception {
        when(patientMapper.toEntity(any(PatientRequest.class))).thenReturn(patient);
        when(patientService.createPatient(any(Patient.class)))
                .thenThrow(new RuntimeException("Patient with identifier already exists"));

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void updatePatient_WithNonExistentId_ReturnsNotFound() throws Exception {
        when(patientMapper.toEntity(any(PatientRequest.class))).thenReturn(patient);
        when(patientService.updatePatient(eq(99L), any(Patient.class)))
                .thenThrow(new RuntimeException("Patient not found"));

        mockMvc.perform(put("/api/patients/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchPatients_WithBirthDateRange_ReturnsResults() throws Exception {
        List<Patient> patients = Arrays.asList(patient);
        List<PatientResponse> responses = Arrays.asList(patientResponse);
        
        when(patientService.searchPatients(anyString(), anyString(), anyString(), 
                                          any(), any(), any())).thenReturn(patients);
        when(patientMapper.toResponseList(patients)).thenReturn(responses);

        mockMvc.perform(get("/api/patients/search")
                .param("birthDateStart", "1980-01-01")
                .param("birthDateEnd", "1990-12-31"))
                .andExpect(status().isOk());
    }
}