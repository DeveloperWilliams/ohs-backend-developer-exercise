package com.countyhospital.healthapi.observation.repository;

import com.countyhospital.healthapi.observation.domain.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {
    
    List<Observation> findByPatientId(Long patientId);
    
    List<Observation> findByEncounterId(Long encounterId);
    
    List<Observation> findByCode(String code);
    
    List<Observation> findByEffectiveDateTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT o FROM Observation o WHERE o.patient.id = :patientId AND o.effectiveDateTime BETWEEN :startDate AND :endDate")
    List<Observation> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Observation o WHERE o.patient.id = :patientId")
    long countByPatientId(@Param("patientId") Long patientId);
}