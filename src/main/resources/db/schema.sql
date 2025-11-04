

-- Patients table
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier VARCHAR(50) NOT NULL UNIQUE,
    given_name VARCHAR(100) NOT NULL,
    family_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'UNKNOWN')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Encounters table
CREATE TABLE IF NOT EXISTS encounters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NULL,
    encounter_class VARCHAR(20) NOT NULL CHECK (encounter_class IN ('INPATIENT', 'OUTPATIENT', 'EMERGENCY', 'VIRTUAL')),
    description VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_encounter_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT chk_encounter_dates CHECK (end_date_time IS NULL OR end_date_time >= start_date_time)
);

-- Observations table
CREATE TABLE IF NOT EXISTS observations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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

-- Indexes
CREATE INDEX IF NOT EXISTS idx_patient_family_name ON patients(family_name);
CREATE INDEX IF NOT EXISTS idx_patient_given_name ON patients(given_name);
CREATE INDEX IF NOT EXISTS idx_patient_identifier ON patients(identifier);
CREATE INDEX IF NOT EXISTS idx_patient_birth_date ON patients(birth_date);

CREATE INDEX IF NOT EXISTS idx_encounter_patient_id ON encounters(patient_id);
CREATE INDEX IF NOT EXISTS idx_encounter_start_date ON encounters(start_date_time);
CREATE INDEX IF NOT EXISTS idx_encounter_class ON encounters(encounter_class);
CREATE INDEX IF NOT EXISTS idx_encounter_date_range ON encounters(start_date_time, end_date_time);

CREATE INDEX IF NOT EXISTS idx_observation_patient_id ON observations(patient_id);
CREATE INDEX IF NOT EXISTS idx_observation_encounter_id ON observations(encounter_id);
CREATE INDEX IF NOT EXISTS idx_observation_effective_date ON observations(effective_date_time);
CREATE INDEX IF NOT EXISTS idx_observation_code ON observations(code);
CREATE INDEX IF NOT EXISTS idx_observation_patient_code_date ON observations(patient_id, code, effective_date_time);

-- Comments
COMMENT ON TABLE patients IS 'Stores patient demographic and identification information';
COMMENT ON COLUMN patients.identifier IS 'Unique patient identifier used in hospital systems';
COMMENT ON COLUMN patients.gender IS 'Biological sex / gender identity: MALE, FEMALE, OTHER, UNKNOWN';

COMMENT ON TABLE encounters IS 'Stores patient visits and encounters with healthcare providers';
COMMENT ON COLUMN encounters.encounter_class IS 'Type of healthcare encounter: INPATIENT, OUTPATIENT, EMERGENCY, VIRTUAL';
COMMENT ON COLUMN encounters.start_date_time IS 'When the encounter began';
COMMENT ON COLUMN encounters.end_date_time IS 'When the encounter ended (optional)';

COMMENT ON TABLE observations IS 'Stores clinical observations, measurements, and lab results';
COMMENT ON COLUMN observations.code IS 'Clinical code (e.g., LOINC code for lab tests)';
COMMENT ON COLUMN observations.display_name IS 'Human readable name for the observation';
COMMENT ON COLUMN observations.value IS 'The actual observation value (numeric or text)';