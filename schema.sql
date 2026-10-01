-- ====================================================================
-- Smart City Noise Pollution Spatial Mapping & Compliance Tracking System
-- Database Schema Script
-- Target Database: MySQL 8.0+ / Compatible RDBMS
-- ====================================================================

CREATE DATABASE IF NOT EXISTS noise_pollution_db;
USE noise_pollution_db;

-- 1. Admin Table
DROP TABLE IF EXISTS noise_records;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS zone_limits;
DROP TABLE IF EXISTS admin;

CREATE TABLE admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Zone Limits Table (Configurable limits as required)
CREATE TABLE zone_limits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    zone_type VARCHAR(50) NOT NULL UNIQUE,
    day_limit_db DECIMAL(5,2) NOT NULL,
    night_limit_db DECIMAL(5,2) NOT NULL,
    standard_limit_db DECIMAL(5,2) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Locations Table (Spatial coordinates & details)
CREATE TABLE locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    zone_type VARCHAR(50) NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_location_name (name),
    INDEX idx_zone_type (zone_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Noise Records Table
CREATE TABLE noise_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location_id BIGINT NULL,
    location_name VARCHAR(100) NOT NULL,
    zone_type VARCHAR(50) NOT NULL,
    noise_level DECIMAL(5,2) NOT NULL,
    record_date DATE NOT NULL,
    record_time TIME NOT NULL,
    limit_applied DECIMAL(5,2) NOT NULL,
    exceedance_db DECIMAL(5,2) NOT NULL,
    compliance_status VARCHAR(20) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_noise_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE SET NULL,
    INDEX idx_record_date (record_date),
    INDEX idx_compliance (compliance_status),
    INDEX idx_noise_zone (zone_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
