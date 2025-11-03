package com.countyhospital.healthapi.patient.repository;

import com.countyhospital.healthapi.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    
    Optional<Patient> findByIdentifier(String identifier);
    
    boolean existsByIdentifier(String identifier);
    
    boolean existsByIdentifierAndIdNot(String identifier, Long id);
    
    @Query("SELECT p FROM Patient p WHERE p.familyName ILIKE %:familyName%")
    List<Patient> findByFamilyNameContainingIgnoreCase(@Param("familyName") String familyName);
    
    @Query("SELECT p FROM Patient p WHERE p.givenName ILIKE %:givenName%")
    List<Patient> findByGivenNameContainingIgnoreCase(@Param("givenName") String givenName);
    
    List<Patient> findByBirthDate(LocalDate birthDate);
    
    List<Patient> findByBirthDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT p FROM Patient p WHERE p.familyName ILIKE %:familyName% AND p.givenName ILIKE %:givenName%")
    List<Patient> findByFamilyNameAndGivenNameContainingIgnoreCase(
            @Param("familyName") String familyName, 
            @Param("givenName") String givenName);
}