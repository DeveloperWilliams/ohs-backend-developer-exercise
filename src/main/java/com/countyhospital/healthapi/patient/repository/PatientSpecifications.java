package com.countyhospital.healthapi.patient.repository;

import com.countyhospital.healthapi.patient.domain.Patient;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public class PatientSpecifications {

    public static Specification<Patient> hasFamilyName(String familyName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(familyName)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("familyName")),
                "%" + familyName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Patient> hasGivenName(String givenName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(givenName)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("givenName")),
                "%" + givenName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Patient> hasIdentifier(String identifier) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(identifier)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("identifier"), identifier);
        };
    }

    public static Specification<Patient> hasBirthDate(LocalDate birthDate) {
        return (root, query, criteriaBuilder) -> {
            if (birthDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("birthDate"), birthDate);
        };
    }

    public static Specification<Patient> hasBirthDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("birthDate"), startDate, endDate);
            }
            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), startDate);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), endDate);
        };
    }

    public static Specification<Patient> searchByCriteria(String familyName, String givenName, 
                                                         String identifier, LocalDate birthDate,
                                                         LocalDate birthDateStart, LocalDate birthDateEnd) {
        return hasFamilyName(familyName)
                .and(hasGivenName(givenName))
                .and(hasIdentifier(identifier))
                .and(hasBirthDate(birthDate))
                .and(hasBirthDateBetween(birthDateStart, birthDateEnd));
    }
}