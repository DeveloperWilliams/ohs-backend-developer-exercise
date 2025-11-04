package com.countyhospital.healthapi.encounter.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.countyhospital.healthapi.encounter.domain.Encounter;
import com.countyhospital.healthapi.patient.domain.Patient;

@Repository
public interface EncounterRepository extends JpaRepository<Encounter, Long>, JpaSpecificationExecutor<Encounter> {
    
    List<Encounter> findByPatient(Patient patient);
    
    List<Encounter> findByPatientId(Long patientId);
    
    List<Encounter> findByEncounterClass(String encounterClass);
    
    List<Encounter> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT e FROM Encounter e WHERE e.patient.id = :patientId AND e.startDateTime BETWEEN :startDate AND :endDate")
    List<Encounter> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(e) FROM Encounter e WHERE e.patient.id = :patientId")
    long countByPatientId(@Param("patientId") Long patientId);
    
    boolean existsByPatientAndStartDateTime(Patient patient, LocalDateTime startDateTime);
}