package com.countyhospital.healthapi.patient.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.countyhospital.healthapi.patient.domain.Patient;
import com.countyhospital.healthapi.patient.dto.request.PatientRequest;
import com.countyhospital.healthapi.patient.dto.response.PatientResponse;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequest request) {
        if (request == null) {
            return null;
        }

        Patient patient = new Patient();
        patient.setIdentifier(request.getIdentifier());
        patient.setGivenName(request.getGivenName());
        patient.setFamilyName(request.getFamilyName());
        patient.setBirthDate(request.getBirthDate());
        patient.setGender(request.getGender());
        
        return patient;
    }

    public PatientResponse toResponse(Patient patient) {
        if (patient == null) {
            return null;
        }

        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setIdentifier(patient.getIdentifier());
        response.setGivenName(patient.getGivenName());
        response.setFamilyName(patient.getFamilyName());
        response.setBirthDate(patient.getBirthDate());
        response.setGender(patient.getGender());
        response.setCreatedAt(patient.getCreatedAt());
        response.setUpdatedAt(patient.getUpdatedAt());
        
        return response;
    }

    public List<PatientResponse> toResponseList(List<Patient> patients) {
        if (patients == null) {
            return null;
        }

        return patients.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntityFromRequest(PatientRequest request, Patient patient) {
        if (request == null || patient == null) {
            return;
        }

        patient.setIdentifier(request.getIdentifier());
        patient.setGivenName(request.getGivenName());
        patient.setFamilyName(request.getFamilyName());
        patient.setBirthDate(request.getBirthDate());
        patient.setGender(request.getGender());
    }
}