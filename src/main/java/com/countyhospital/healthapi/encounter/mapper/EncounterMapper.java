package com.countyhospital.healthapi.encounter.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.countyhospital.healthapi.encounter.domain.Encounter;
import com.countyhospital.healthapi.encounter.dto.request.EncounterRequest;
import com.countyhospital.healthapi.encounter.dto.response.EncounterResponse;
import com.countyhospital.healthapi.patient.domain.Patient;

@Component
public class EncounterMapper {

    public Encounter toEntity(EncounterRequest request, Patient patient) {
        if (request == null) {
            return null;
        }

        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setStartDateTime(request.getStartDateTime());
        encounter.setEndDateTime(request.getEndDateTime());
        encounter.setEncounterClass(request.getEncounterClass());
        encounter.setDescription(request.getDescription());
        
        return encounter;
    }

    public EncounterResponse toResponse(Encounter encounter) {
        if (encounter == null) {
            return null;
        }

        EncounterResponse response = new EncounterResponse();
        response.setId(encounter.getId());
        
        if (encounter.getPatient() != null) {
            response.setPatientId(encounter.getPatient().getId());
            response.setPatientGivenName(encounter.getPatient().getGivenName());
            response.setPatientFamilyName(encounter.getPatient().getFamilyName());
        }
        
        response.setStartDateTime(encounter.getStartDateTime());
        response.setEndDateTime(encounter.getEndDateTime());
        response.setEncounterClass(encounter.getEncounterClass());
        response.setDescription(encounter.getDescription());
        response.setCreatedAt(encounter.getCreatedAt());
        response.setUpdatedAt(encounter.getUpdatedAt());
        
        return response;
    }

    public List<EncounterResponse> toResponseList(List<Encounter> encounters) {
        if (encounters == null) {
            return null;
        }

        return encounters.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntityFromRequest(EncounterRequest request, Encounter encounter, Patient patient) {
        if (request == null || encounter == null) {
            return;
        }

        encounter.setPatient(patient);
        encounter.setStartDateTime(request.getStartDateTime());
        encounter.setEndDateTime(request.getEndDateTime());
        encounter.setEncounterClass(request.getEncounterClass());
        encounter.setDescription(request.getDescription());
    }
}