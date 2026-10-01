-- ====================================================================
-- Smart City Noise Pollution Spatial Mapping & Compliance Tracking System
-- Initial Seed Data
-- ====================================================================

USE noise_pollution_db;

-- 1. Default Admin Credentials (Username: admin, Password: admin123)
INSERT INTO admin (username, password, full_name, role) VALUES 
('admin', 'admin123', 'City Environmental Officer', 'ADMIN');

-- 2. Statutory Environmental Zone Noise Limits
INSERT INTO zone_limits (zone_type, day_limit_db, night_limit_db, standard_limit_db, description) VALUES
('Silence Zone', 50.0, 40.0, 50.0, 'Hospital, educational institution, and court zones (100m radius)'),
('Residential', 55.0, 45.0, 55.0, 'Areas designated predominantly for residential living'),
('Commercial', 65.0, 55.0, 65.0, 'Retail, business districts, markets, and transit hubs'),
('Industrial', 75.0, 70.0, 75.0, 'Manufacturing hubs and industrial development estates');

-- 3. Monitoring Locations with Real Spatial Coordinates (City Center Coordinates)
INSERT INTO locations (id, name, zone_type, latitude, longitude, address) VALUES
(1, 'City General Hospital', 'Silence Zone', 12.9716000, 77.5946000, 'Sector 4, Healthcare Avenue'),
(2, 'Green Valley Public School', 'Silence Zone', 12.9650000, 77.5850000, 'Ring Road East, Academic Enclave'),
(3, 'Sunshine Palms Residency', 'Residential', 12.9352000, 77.6245000, 'Block B, 5th Main, Green Park'),
(4, 'Lakeside Garden Apartments', 'Residential', 12.9550000, 77.6400000, 'Lake Promenade, Zone 2'),
(5, 'Central Grand Mall & Station', 'Commercial', 12.9780000, 77.6010000, 'MG Road Junction, Central CBD'),
(6, 'Tech Park Boulevard', 'Commercial', 12.9260000, 77.6830000, 'Silicon Corridor, Outer Ring Road'),
(7, 'West Heavy Industrial Estate', 'Industrial', 13.0300000, 77.5200000, 'Phase 1, Manufacturing Belt'),
(8, 'Auto Ancillary Complex', 'Industrial', 13.0450000, 77.5100000, 'Expressway Exit 7, Industrial Corridor');

-- 4. Initial Baseline Noise Monitoring Records
INSERT INTO noise_records (location_id, location_name, zone_type, noise_level, record_date, record_time, limit_applied, exceedance_db, compliance_status, description) VALUES
(1, 'City General Hospital', 'Silence Zone', 46.5, CURDATE(), '09:30:00', 50.0, 0.0, 'SAFE', 'Routine morning monitoring outside OPD block. Ambient levels within silence zone norms.'),
(1, 'City General Hospital', 'Silence Zone', 62.8, CURDATE(), '14:15:00', 50.0, 12.8, 'NON_COMPLIANT', 'Traffic honking and nearby construction activity caused severe threshold violation.'),
(2, 'Green Valley Public School', 'Silence Zone', 48.0, CURDATE(), '11:00:00', 50.0, 0.0, 'SAFE', 'School hours acoustic check. Normal background sound level.'),
(3, 'Sunshine Palms Residency', 'Residential', 52.3, CURDATE(), '08:00:00', 55.0, 0.0, 'SAFE', 'Residential quiet hours inspection. Compliant with residential baseline.'),
(3, 'Sunshine Palms Residency', 'Residential', 68.4, CURDATE(), '20:45:00', 55.0, 13.4, 'NON_COMPLIANT', 'Public event loudspeaker use without acoustic shields. Exceeded safe limit.'),
(4, 'Lakeside Garden Apartments', 'Residential', 54.1, CURDATE(), '10:30:00', 55.0, 0.0, 'SAFE', 'Normal morning neighborhood acoustic footprint.'),
(5, 'Central Grand Mall & Station', 'Commercial', 63.5, CURDATE(), '12:00:00', 65.0, 0.0, 'SAFE', 'Mid-day shoppers transit flow. Within permitted commercial envelope.'),
(5, 'Central Grand Mall & Station', 'Commercial', 77.2, CURDATE(), '18:30:00', 65.0, 12.2, 'NON_COMPLIANT', 'Peak evening rush hour combined with generator exhaust noise.'),
(6, 'Tech Park Boulevard', 'Commercial', 61.0, CURDATE(), '15:00:00', 65.0, 0.0, 'SAFE', 'Campus perimeter noise monitoring. Controlled traffic flow.'),
(7, 'West Heavy Industrial Estate', 'Industrial', 73.5, CURDATE(), '11:30:00', 75.0, 0.0, 'SAFE', 'Standard manufacturing operations during daytime shift.'),
(7, 'West Heavy Industrial Estate', 'Industrial', 84.0, CURDATE(), '16:00:00', 75.0, 9.0, 'NON_COMPLIANT', 'Metal stamping unit unshielded machinery operation caused spike.'),
(8, 'Auto Ancillary Complex', 'Industrial', 71.8, CURDATE(), '13:45:00', 75.0, 0.0, 'SAFE', 'Assembly line operations within compliance boundary.');
