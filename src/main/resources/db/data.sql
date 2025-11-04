-- Sample patients
INSERT INTO patients (identifier, given_name, family_name, birth_date, gender, created_at, updated_at) VALUES
('PAT-1001', 'John', 'Smith', '1985-03-15', 'MALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PAT-1002', 'Maria', 'Garcia', '1978-07-22', 'FEMALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PAT-1003', 'David', 'Johnson', '1992-11-30', 'MALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PAT-1004', 'Sarah', 'Williams', '1988-05-14', 'FEMALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PAT-1005', 'James', 'Brown', '1975-12-03', 'MALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PAT-1006', 'Lisa', 'Davis', '1995-09-18', 'FEMALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PAT-1007', 'Robert', 'Miller', '1965-02-28', 'MALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PAT-1008', 'Jennifer', 'Wilson', '1982-08-11', 'FEMALE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample encounters
INSERT INTO encounters (patient_id, start_date_time, end_date_time, encounter_class, description, created_at, updated_at) VALUES
(1, '2024-01-10 09:00:00', '2024-01-10 10:30:00', 'OUTPATIENT', 'Annual physical examination'),
(1, '2024-01-15 14:15:00', '2024-01-15 15:00:00', 'OUTPATIENT', 'Follow-up consultation'),
(2, '2024-01-12 10:30:00', '2024-01-12 11:45:00', 'OUTPATIENT', 'Vaccination appointment'),
(3, '2024-01-08 08:00:00', '2024-01-10 17:00:00', 'INPATIENT', 'Surgical procedure and recovery'),
(4, '2024-01-14 16:20:00', '2024-01-14 17:10:00', 'VIRTUAL', 'Telemedicine consultation'),
(5, '2024-01-09 22:15:00', '2024-01-10 02:30:00', 'EMERGENCY', 'Emergency room visit for abdominal pain'),
(2, '2024-01-18 13:00:00', '2024-01-18 13:45:00', 'OUTPATIENT', 'Lab results review'),
(6, '2024-01-11 11:00:00', '2024-01-11 11:30:00', 'OUTPATIENT', 'Blood pressure check');

-- Sample observations
INSERT INTO observations (patient_id, encounter_id, code, display_name, value, unit, effective_date_time, created_at) VALUES
(1, 1, '8310-5', 'Body Temperature', '98.6', '°F', '2024-01-10 09:15:00', CURRENT_TIMESTAMP),
(1, 1, '8867-4', 'Heart Rate', '72', 'beats/min', '2024-01-10 09:20:00', CURRENT_TIMESTAMP),
(1, 1, '8462-4', 'Blood Pressure', '120/80', 'mm[Hg]', '2024-01-10 09:25:00', CURRENT_TIMESTAMP),
(1, 1, '8302-2', 'Body Height', '70', 'in', '2024-01-10 09:30:00', CURRENT_TIMESTAMP),
(1, 1, '29463-7', 'Body Weight', '175', 'lb', '2024-01-10 09:35:00', CURRENT_TIMESTAMP),

(2, 3, '56799-0', 'Influenza Vaccine', 'Administered', null, '2024-01-12 11:00:00', CURRENT_TIMESTAMP),
(2, 7, '789-8', 'Hemoglobin', '14.2', 'g/dL', '2024-01-18 13:15:00', CURRENT_TIMESTAMP),

(3, 4, '2345-7', 'Glucose', '95', 'mg/dL', '2024-01-09 06:00:00', CURRENT_TIMESTAMP),
(3, 4, '2160-0', 'Creatinine', '0.9', 'mg/dL', '2024-01-09 06:15:00', CURRENT_TIMESTAMP),

(5, 6, '8480-6', 'Blood Pressure', '145/95', 'mm[Hg]', '2024-01-09 22:30:00', CURRENT_TIMESTAMP),
(5, 6, '8867-4', 'Heart Rate', '88', 'beats/min', '2024-01-09 22:35:00', CURRENT_TIMESTAMP),

(6, 8, '8462-4', 'Blood Pressure', '118/78', 'mm[Hg]', '2024-01-11 11:15:00', CURRENT_TIMESTAMP);