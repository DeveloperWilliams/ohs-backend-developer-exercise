package com.countyhospital.healthapi.encounter.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.countyhospital.healthapi.encounter.domain.Encounter;

public interface EncounterService {
    
    Encounter createEncounter(Encounter encounter);
    
    Encounter getEncounterById(Long id);
    
    List<Encounter> getEncountersByPatientId(Long patientId);
    
    Page<Encounter> getEncountersByPatientId(Long patientId, Pageable pageable);
    
    List<Encounter> getAllEncounters();
    
    Page<Encounter> getAllEncounters(Pageable pageable);
    
    List<Encounter> getEncountersByDateRange(Instant start, Instant end);
    
    List<Encounter> getEncountersByPatientIdAndDateRange(Long patientId, Instant start, Instant end);
    
    List<Encounter> getEncountersByClass(String encounterClass);
    
    Encounter updateEncounter(Long id, Encounter encounterDetails);
    
    void deleteEncounter(Long id);
    
    boolean encounterExists(Long id);
    
    long getEncounterCountByPatientId(Long patientId);
}