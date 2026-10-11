-- ==========================================================
-- City Brain — Unified Production Database Schema & Seed Script
-- Compatible with MySQL 8.0+, TiDB, Aiven, Railway, AWS RDS
-- ==========================================================

CREATE DATABASE IF NOT EXISTS citybrain CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE citybrain;

-- 1. App Users (Authentication & Roles)
CREATE TABLE IF NOT EXISTS app_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Traffic Flow Nodes (Ground Truth)
CREATE TABLE IF NOT EXISTS traffic (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    congestion INT NOT NULL,
    vehicles INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Weather Stations (Ground Truth)
CREATE TABLE IF NOT EXISTS weather (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    temperature DOUBLE NOT NULL,
    humidity INT NOT NULL,
    rainfall DOUBLE NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Citizen Complaints
CREATE TABLE IF NOT EXISTS citizen_complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    citizen_name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL,
    priority VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Emergency Incidents
CREATE TABLE IF NOT EXISTS emergency_incidents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL,
    severity VARCHAR(50),
    description TEXT,
    status VARCHAR(50),
    reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Water Network Issues
CREATE TABLE IF NOT EXISTS water_issues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    issue_type VARCHAR(100),
    severity VARCHAR(50),
    description TEXT,
    status VARCHAR(50),
    reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Solid Waste Records
CREATE TABLE IF NOT EXISTS waste_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    waste_type VARCHAR(100),
    quantity DOUBLE,
    status VARCHAR(50),
    collected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Air Quality & Pollution Records
CREATE TABLE IF NOT EXISTS pollution_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    aqi INT,
    pm25 DOUBLE,
    pm10 DOUBLE,
    status VARCHAR(50),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Predictive Urban Risks
CREATE TABLE IF NOT EXISTS risk_predictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    risk_type VARCHAR(100),
    risk_level VARCHAR(50),
    probability DOUBLE,
    description TEXT,
    predicted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


