package com.countyhospital.healthapi.patient.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.countyhospital.healthapi.patient.domain.Patient;

public interface PatientService {
    
    Patient createPatient(Patient patient);
    
    Patient getPatientById(Long id);
    
    Patient getPatientByIdentifier(String identifier);
    
    Page<Patient> getAllPatients(Pageable pageable);
    
    List<Patient> searchPatients(String familyName, String givenName, String identifier, 
                                LocalDate birthDate, LocalDate birthDateStart, LocalDate birthDateEnd);
    
    Patient updatePatient(Long id, Patient patientDetails);
    
    void deletePatient(Long id);
    
    boolean patientExists(Long id);
    
    boolean identifierExists(String identifier);
    
    boolean identifierExistsForOtherPatient(String identifier, Long excludedPatientId);
    
    Page<Patient> searchPatients(Specification<Patient> spec, Pageable pageable);
}