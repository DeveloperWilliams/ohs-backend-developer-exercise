-- Flyway migration script for initial schema

-- Create sequences for ID generation
CREATE SEQUENCE IF NOT EXISTS patient_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS encounter_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS observation_seq START WITH 1 INCREMENT BY 50;

-- Patients table
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT DEFAULT NEXT VALUE FOR patient_seq PRIMARY KEY,
    identifier VARCHAR(50) NOT NULL UNIQUE,
    given_name VARCHAR(100) NOT NULL,
    family_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Encounters table
CREATE TABLE IF NOT EXISTS encounters (
    id BIGINT DEFAULT NEXT VALUE FOR encounter_seq PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NULL,
    encounter_class VARCHAR(20) NOT NULL,
    description VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_encounter_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- Observations table
CREATE TABLE IF NOT EXISTS observations (
    id BIGINT DEFAULT NEXT VALUE FOR observation_seq PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    encounter_id BIGINT NULL,
    code VARCHAR(50) NOT NULL,
    display_name VARCHAR(200) NOT NULL,
    "value" VARCHAR(500) NOT NULL,
    unit VARCHAR(50) NULL,
    effective_date_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_observation_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_observation_encounter FOREIGN KEY (encounter_id) REFERENCES encounters(id) ON DELETE SET NULL
);

-- Create indexes
CREATE INDEX idx_patient_family_name ON patients(family_name);
CREATE INDEX idx_patient_given_name ON patients(given_name);
CREATE INDEX idx_patient_identifier ON patients(identifier);
CREATE INDEX idx_patient_birth_date ON patients(birth_date);

CREATE INDEX idx_encounter_patient_id ON encounters(patient_id);
CREATE INDEX idx_encounter_start_date ON encounters(start_date_time);
CREATE INDEX idx_encounter_class ON encounters(encounter_class);

CREATE INDEX idx_observation_patient_id ON observations(patient_id);
CREATE INDEX idx_observation_encounter_id ON observations(encounter_id);
CREATE INDEX idx_observation_effective_date ON observations(effective_date_time);
CREATE INDEX idx_observation_code ON observations(code);