package com.countyhospital.healthapi.encounter.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.countyhospital.healthapi.encounter.domain.Encounter;
import com.countyhospital.healthapi.patient.domain.Patient;

@Repository
public interface EncounterRepository extends JpaRepository<Encounter, Long> {
    
    List<Encounter> findByPatientId(Long patientId);
    
    Page<Encounter> findByPatientId(Long patientId, Pageable pageable);
    
    List<Encounter> findByStartDateTimeBetween(Instant start, Instant end);
    
    List<Encounter> findByPatientIdAndStartDateTimeBetween(Long patientId, Instant start, Instant end);
    
    List<Encounter> findByEncounterClass(String encounterClass);
    
    boolean existsByPatientAndStartDateTime(Patient patient, Instant startDateTime);
    
    long countByPatientId(Long patientId);
}