# Smart City Noise Pollution Spatial Mapping System and Compliance Tracking Dashboard

A comprehensive, full-stack, web-based environmental monitoring and municipal compliance tracking dashboard designed for smart cities. The system allows city environmental officers and administrators to track noise pollution levels across various urban zones, visualize real-time acoustic hotspot maps, evaluate statutory compliance against environmental standards, manage noise inspection records, and batch-import multi-location CSV datasets—**without requiring any IoT devices or physical sensors**.

---

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Key Features](#key-features)
4. [Architecture & Workflow](#architecture--workflow)
5. [Database Design & Schema](#database-design--schema)
6. [Configuring MySQL & MySQL Workbench](#configuring-mysql--mysql-workbench)
7. [Running the Project](#running-the-project)
   - [Option A: Running with Maven Wrapper (Command Line)](#option-a-running-with-maven-wrapper-command-line)
   - [Option B: Running in VS Code](#option-b-running-in-vs-code)
   - [Option C: Running in IntelliJ IDEA](#option-c-running-in-intellij-idea)
8. [REST API Documentation](#rest-api-documentation)
9. [Sample CSV Dataset](#sample-csv-dataset)
10. [Step-by-Step Testing & Viva Demonstration](#step-by-step-testing--viva-demonstration)
11. [Project File Structure & Explanation](#project-file-structure--explanation)

---

## 1. Project Overview

Municipal authorities and environmental boards mandate statutory limits on ambient noise levels depending on the zone (Silence Zone, Residential, Commercial, and Industrial) and time of day. This mini-project automates compliance auditing, spatial mapping, and analytics using software intelligence:

- **100% Software-Driven**: Uses manual inspection logs and CSV dataset imports (Zero hardware / Arduino / Raspberry Pi dependency).
- **Statutory Compliance Engine**: Real-time evaluation comparing dB readings against dynamic database thresholds to tag each record as `SAFE` (Compliant) or `NON_COMPLIANT` (Exceeded).
- **Interactive Spatial Mapping**: Interactive city map (using Leaflet.js and OpenStreetMap) showing locations with pulsating green and red radar markers.
- **Analytical Reports & Charts**: Visualizes compliance ratios, zone comparisons, daily noise trends, and top 5 loudest violations.

---

## 2. Technology Stack

- **Frontend**: HTML5, CSS3 (Modern Glassmorphism & Cyber Theme), JavaScript (ES6 Modules), Bootstrap 5, Leaflet.js (OpenStreetMap), Chart.js.
- **Backend**: Java 17, Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Hibernate, Bean Validation, OpenCSV).
- **Database**: MySQL 8.0 / MySQL Workbench (also includes embedded H2 MySQL-compatibility mode for zero-setup execution).
- **Build Tool**: Apache Maven (`mvnw` wrapper included).

---

## 3. Key Features

### A. Admin Authentication
- Secure admin login portal (`login.html`) with session management.
- Pre-seeded admin account: `admin` / `admin123` with a "Quick Fill Demo Credentials" button.

### B. Executive Dashboard
- 6 Real-time KPI Metric Cards:
  1. **Total Monitoring Locations**
  2. **Total Noise Records**
  3. **Safe / Compliant Sites**
  4. **Non-Compliant Hotspots**
  5. **Average Noise Level (dB)**
  6. **Peak Noise Level (dB)** with location indicator
- Interactive Mini Map with status pins.
- Visual Doughnut Chart for Compliance Ratio.
- Zone-wise Decibel Comparison vs Statutory Ceiling Bar Chart.
- Daily Noise Level Timeline Chart.
- Recent Records stream with status tags.

### C. Manual Noise Reading Form & Live Preview Gauge
- Enter Location Name (with intelligent autocomplete datalist), Zone Category, Date, Time, Noise (dB), Coordinates (Latitude/Longitude), and Observation Notes.
- **Live Gauge Preview**: Dynamically previews the active statutory limit, variance/exceedance, and tags the reading as `SAFE` or `NON_COMPLIANT` before saving.

### D. Dataset Support (CSV Import)
- Drag-and-drop CSV dataset batch uploader.
- Automatically validates row fields, creates or links locations, evaluates compliance for every entry, and reports import counts and error summaries.
- Includes pre-built `sample_noise_data.csv`.

### E. Spatial Mapping System
- Full-screen spatial map (`map.html`) powered by Leaflet.js.
- Color-coded pins: **Green** for Compliant sites, **Pulsing Red** for Hotspots with threshold violations.
- Popups displaying location name, zone, dB level, permitted limit, exceedance, date/time, and GPS coordinates.
- Sidebar search and filter pills (All, Silence, Residential, Commercial, Industrial, Violations Only).
- Click-to-fly feature that focuses and zooms the map onto selected sites.

### F. Records Management
- Complete searchable data table (`records.html`).
- Filter by Zone, Compliance Status, and Date Range.
- Search by location name or description.
- Inline **Edit Modal** (automatically recalculates compliance on update).
- **Delete Record** with confirmation modal.
- **Export Filtered Records to CSV** button.

### G. Statutory Reports & Threshold Governance
- City compliance ratio analysis and top 5 loudest violations ranking.
- **Configurable Zone Limits Table**: Allows administrators to dynamically update Day/Night/Standard statutory thresholds per zone without changing any code.
- One-click **Print Audit Report** with print-optimized CSS.

---

## 4. Architecture & Workflow

```
[ Admin Login ]
       │
       ▼
[ Dashboard ] ───► [ Live KPIs + Mini Map + Charts + Recent Feeds ]
       │
       ├───────────────────────────────┐
       ▼                               ▼
[ Add Noise Form ]             [ CSV Batch Import ]
       │                               │
       └──────────────┬────────────────┘
                      ▼
        [ Spring Boot REST API ]
                      │
                      ▼
        [ Compliance Engine ]
        (Compares dB with zone_limits table)
        (Calculates Exceedance & Tag: SAFE / NON_COMPLIANT)
                      │
                      ▼
        [ MySQL Database Storage ]
                      │
                      ▼
[ Spatial Map ]  [ Records Table ]  [ Analytical Reports ]
```

---

## 5. Database Design & Schema

The database `noise_pollution_db` consists of 4 relational tables:

```
+-------------------+       1:N       +---------------------+
|     locations     | <-------------- |    noise_records    |
+-------------------+                 +---------------------+
| id (PK)           |                 | id (PK)             |
| name (UNIQUE)     |                 | location_id (FK)    |
| zone_type         |                 | location_name       |
| latitude          |                 | zone_type           |
| longitude         |                 | noise_level         |
| address           |                 | record_date         |
| created_at        |                 | record_time         |
+-------------------+                 | limit_applied       |
                                      | exceedance_db       |
+-------------------+                 | compliance_status   |
|    zone_limits    |                 | description         |
+-------------------+                 | created_at          |
| id (PK)           |                 +---------------------+
| zone_type (UNIQUE)|
| day_limit_db      |                 +---------------------+
| night_limit_db    |                 |        admin        |
| standard_limit_db |                 +---------------------+
| description       |                 | id (PK)             |
+-------------------+                 | username (UNIQUE)   |
                                      | password            |
                                      | full_name           |
                                      | role                |
                                      | created_at          |
                                      +---------------------+
```

### Statutory Ambient Air Quality Standards (CPCB Norms):
| Zone Category | Day Limit (06:00 – 22:00) | Night Limit (22:00 – 06:00) | Baseline Limit |
|:---|:---:|:---:|:---:|
| **Silence Zone** | 50.0 dB | 40.0 dB | 50.0 dB |
| **Residential** | 55.0 dB | 45.0 dB | 55.0 dB |
| **Commercial** | 65.0 dB | 55.0 dB | 65.0 dB |
| **Industrial** | 75.0 dB | 70.0 dB | 75.0 dB |

---

## 6. Configuring MySQL & MySQL Workbench

### Step 1: Open MySQL Workbench
1. Launch **MySQL Workbench**.
2. Connect to your local MySQL instance (e.g. `Local instance MySQL80` on port `3306`).
3. Click **File -> Open SQL Script...** and select `schema.sql` from the project root folder.
4. Click the **Execute (Lightning icon)** button to create the `noise_pollution_db` database and its tables.
5. Next, open `data.sql` and execute it to populate initial seed records.

### Step 2: Configure Spring Boot Connection
In `src/main/resources/application.properties`:
```properties
# Change the active profile to 'mysql':
spring.profiles.active=mysql
```
In `src/main/resources/application-mysql.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/noise_pollution_db?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
# Enter your MySQL root password here:
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

> **Note on Instant Execution**: The application is configured by default with `spring.profiles.active=dev`, which runs an embedded MySQL-mode in-memory database with automatic seeding. This means the project can be run and tested immediately out-of-the-box without waiting for database credentials!

---

## 7. Running the Project

### Option A: Running Backend & Frontend in Two Separate Terminals (Recommended)

Since Python is already installed on your system, you can run the backend and frontend in two independent terminals:

#### **Terminal 1 (Backend - Spring Boot on Port 8080)**:
```powershell
cd c:\Users\sanja\OneDrive\Desktop\project
.\mvnw.cmd spring-boot:run
```
> Wait until you see: `Started NoiseMonitorApplication in X.XXX seconds`. Your REST APIs are live on `http://localhost:8080/api`.

#### **Terminal 2 (Frontend - Live HTTP Server on Port 3000)**:
Open a second terminal window in VS Code / PowerShell:
```powershell
cd c:\Users\sanja\OneDrive\Desktop\project\frontend
python -m http.server 3000
```
> Now open your browser and navigate to:
> **[http://localhost:3000/](http://localhost:3000/)**
> 
> *Note*: The frontend automatically detects port 3000 and connects to the Spring Boot REST API at `http://localhost:8080/api` with full CORS support enabled!

---

### Option B: Running Everything in a Single Terminal
If you prefer running everything in a single terminal, Spring Boot also hosts the frontend directly:
```powershell
cd c:\Users\sanja\OneDrive\Desktop\project
.\mvnw.cmd spring-boot:run
```
Open: `http://localhost:8080/`

### Option B: Running in VS Code
1. Open VS Code: `File -> Open Folder...` and select `c:\Users\sanja\OneDrive\Desktop\project`.
2. Install the **Extension Pack for Java** and **Spring Boot Extension Pack** if not already installed.
3. Open `src/main/java/com/smartcity/noisemonitor/NoiseMonitorApplication.java`.
4. Click the **Run** button above the `main` method or press `F5`.
5. Access `http://localhost:8080/` in your browser.

### Option C: Running in IntelliJ IDEA
1. Open IntelliJ IDEA: `File -> Open...` and select the project folder.
2. Select **Open as Maven Project**.
3. Allow IntelliJ to download dependencies.
4. Navigate to `com.smartcity.noisemonitor.NoiseMonitorApplication` and click the green **Run** arrow.
5. Access `http://localhost:8080/` in your browser.

---

## 8. REST API Documentation

| Method | Endpoint | Description | Sample Parameters / Body |
|:---|:---|:---|:---|
| `POST` | `/api/auth/login` | Authenticate admin | `{"username":"admin","password":"admin123"}` |
| `GET` | `/api/dashboard/stats` | KPI counters & summary | None |
| `GET` | `/api/dashboard/zone-summary` | Zone-wise noise & counts | None |
| `GET` | `/api/dashboard/daily-trends` | Daily mean & peak dB | None |
| `GET` | `/api/dashboard/top-violations`| Top 5 loudest violations | None |
| `GET` | `/api/locations` | All monitoring locations | None |
| `GET` | `/api/locations/spatial` | Enriched locations for map | None |
| `POST`| `/api/locations` | Register new location | `{"name":"...","zoneType":"...","latitude":12.9,"longitude":77.5}` |
| `GET` | `/api/noise-records` | Filter records | `?search=...&zoneType=...&complianceStatus=...&startDate=...&endDate=...` |
| `GET` | `/api/noise-records/recent` | 10 most recent records | None |
| `GET` | `/api/noise-records/{id}` | Get record by ID | Path variable `id` |
| `POST`| `/api/noise-records` | Save noise record & evaluate | `{"locationName":"...","zoneType":"...","noiseLevel":65.5,"recordDate":"2026-09-19","recordTime":"14:00"}` |
| `PUT` | `/api/noise-records/{id}` | Update record & re-evaluate | Updated record JSON |
| `DELETE`| `/api/noise-records/{id}`| Delete record | Path variable `id` |
| `GET` | `/api/zone-limits` | Statutory zone limits | None |
| `PUT` | `/api/zone-limits/{id}` | Update statutory threshold | `{"zoneType":"...","dayLimitDb":50.0,"nightLimitDb":40.0}` |
| `POST`| `/api/csv/import` | Upload CSV dataset | Multipart form data with `file` |
| `GET` | `/api/csv/sample-download` | Download CSV template | None |

---

## 9. Sample CSV Dataset

The project includes `sample_noise_data.csv` in the root folder with 15 pre-configured records:

```csv
location_name,zone_type,date,time,noise_level,latitude,longitude,description
Apollo Super Specialty Hospital,Silence Zone,2026-09-15,10:15,44.2,12.9602,77.5912,Silence zone check outside emergency trauma care unit.
National Institute of Mental Health,Silence Zone,2026-09-15,11:30,61.5,12.9395,77.5950,Noise spike observed due to emergency ambulance sirens and nearby road drilling.
Brigade Gateway Residency,Residential,2026-09-16,07:45,51.2,13.0118,77.5552,Early morning neighborhood acoustic check. Very calm and compliant.
Koramangala 4th Block,Residential,2026-09-16,21:15,72.4,12.9340,77.6280,Late evening violation caused by open-air restaurant music speakers.
Indiranagar 100ft Road,Commercial,2026-09-16,14:30,64.1,12.9784,77.6408,Afternoon commercial district traffic and shopping crowd. Compliant.
MG Road Metro Promenade,Commercial,2026-09-16,19:00,81.3,12.9756,77.6066,Heavy vehicle congestion and metro construction machinery exceedance.
Peenya Industrial Area Stage 2,Industrial,2026-09-17,11:45,74.2,13.0280,77.5180,Forging and CNC machining facility operations within permissible ceiling.
Bommasandra Heavy Engineering Park,Industrial,2026-09-17,15:30,88.6,12.8160,77.6780,High-tonnage stamping press operation exceeded statutory industrial limits.
```

---

## 10. Step-by-Step Testing & Viva Demonstration

During your project demonstration or viva, follow these steps:

1. **Demonstrate Login**:
   - Go to `http://localhost:8080/login.html`.
   - Click **Quick Fill Demo Credentials** (`admin` / `admin123`) and click **Sign In**.
   - Show how the session is stored and redirects to the Dashboard.

2. **Demonstrate Dashboard**:
   - Point out the **6 KPI cards** (Total locations, total records, safe vs violation counts, average dB, and peak dB hotspot).
   - Explain the **Compliance Doughnut Chart**, the **Zone-wise Mean dB vs Limit Bar Chart**, and the **Daily Trend Line Chart**.
   - Show the interactive **Mini Map** pins.

3. **Demonstrate Compliance Logic via Manual Entry**:
   - Click **Add Noise Data** in the navbar (`add-noise.html`).
   - Select **Silence Zone** (Day limit is 50.0 dB).
   - In the **Noise Level (dB)** field, type `45.0` -> show the live preview gauge turning **Green** (`SAFE / COMPLIANT`).
   - Now type `68.0` in the same field -> show the gauge immediately turning **Red** (`EXCEEDED / NON-COMPLIANT` by +18.0 dB).
   - Fill in a test location name (e.g. `City Eye Care Hospital`) and click **Save & Check Compliance**.
   - Show the success alert confirming that the backend calculated the exceedance and tagged it as `NON_COMPLIANT`.

4. **Demonstrate CSV Batch Import**:
   - In the right-hand panel of `add-noise.html`, drag and drop `sample_noise_data.csv` (or click to browse).
   - Click **Process & Import Dataset**.
   - Show the summary dialog reporting the number of imported records and any validation checks.

5. **Demonstrate Spatial Mapping**:
   - Click **Spatial Map** (`map.html`).
   - Explain the OpenStreetMap integration with **green pins (Compliant)** and **pulsing red pins (Violating Hotspots)**.
   - Click on any location in the sidebar to fly directly to that pin and show the popup with coordinates and exceedance values.
   - Filter by **Silence Zone** or **Violations** using the filter buttons.

6. **Demonstrate Records Management**:
   - Click **Noise Records** (`records.html`).
   - Type a keyword into the search bar (e.g., `Hospital`) -> records filter in real time.
   - Filter by **Commercial** zone and **NON_COMPLIANT** status.
   - Click the **Edit** button on any record -> change the dB value and save -> show how the compliance status updates.
   - Click the **Export Filtered CSV** button to download a spreadsheet report.

7. **Demonstrate Reports & Threshold Governance**:
   - Click **Reports** (`reports.html`).
   - Show the **Top 5 Loudest City Hotspots** table.
   - Show the **Configurable Statutory Zone Thresholds** table.
   - Click **Edit** on `Silence Zone` -> change Day Limit to `52.0 dB` -> save -> show the table and compliance engine immediately adapting to the new threshold!
   - Click **Print Audit Report** to demonstrate the printable executive layout.

---

## 11. Project File Structure & Explanation

```
project/
├── .mvn/                                      # Maven wrapper binaries
├── mvnw.cmd                                   # Windows Maven wrapper executable
├── pom.xml                                    # Maven project definition & dependencies
├── schema.sql                                 # Complete MySQL database DDL schema script
├── data.sql                                   # Initial SQL seed data script
├── sample_noise_data.csv                      # Sample realistic smart city CSV dataset
├── README.md                                  # Complete documentation & viva guide
└── src/
    └── main/
        ├── java/com/smartcity/noisemonitor/
        │   ├── NoiseMonitorApplication.java   # Spring Boot main application class
        │   ├── config/
        │   │   ├── CorsConfig.java            # CORS cross-origin configuration & static routing
        │   │   └── DataInitializer.java       # Automatic DB seed runner for instant testing
        │   ├── controller/
        │   │   ├── AdminAuthController.java   # Admin login API endpoint
        │   │   ├── DashboardController.java   # Analytics & KPI endpoints
        │   │   ├── NoiseRecordController.java # CRUD operations & search/filter API
        │   │   ├── LocationController.java    # Spatial coordinates & map feed API
        │   │   ├── ZoneLimitController.java   # Dynamic statutory threshold API
        │   │   └── CsvImportController.java   # CSV multipart file upload API
        │   ├── dto/
        │   │   ├── ApiResponse.java           # Standard API response envelope
        │   │   ├── LoginRequest.java          # Login payload
        │   │   ├── LoginResponse.java         # Auth token & user info
        │   │   ├── NoiseRecordRequest.java    # Validated noise reading payload
        │   │   ├── DashboardStatsDto.java     # KPI summary data transfer object
        │   │   ├── ZoneSummaryDto.java        # Zone breakdown data transfer object
        │   │   ├── SpatialLocationDto.java    # Map marker payload with latest readings
        │   │   └── DailyTrendDto.java         # Chronological trend data transfer object
        │   ├── model/
        │   │   ├── Admin.java                 # JPA Entity for admin table
        │   │   ├── ZoneLimit.java             # JPA Entity for configurable zone limits
        │   │   ├── Location.java              # JPA Entity for monitoring locations & coordinates
        │   │   └── NoiseRecord.java           # JPA Entity for noise inspection logs & compliance
        │   ├── repository/
        │   │   ├── AdminRepository.java       # JPA repository for admin queries
        │   │   ├── ZoneLimitRepository.java   # JPA repository for zone threshold lookups
        │   │   ├── LocationRepository.java    # JPA repository for spatial location queries
        │   │   └── NoiseRecordRepository.java # Custom queries for filtering, averages, and rankings
        │   └── service/
        │       ├── AuthService.java           # Admin verification service
        │       ├── ComplianceService.java     # Statutory compliance calculation engine
        │       ├── ZoneLimitService.java      # Threshold management service
        │       ├── LocationService.java       # Spatial map feed service
        │       ├── NoiseRecordService.java    # Business logic for CRUD and filtering
        │       ├── DashboardService.java      # KPI aggregation and chart computations
        │       └── CsvImportService.java      # CSV parsing, validation, and batch persistence
        └── resources/
            ├── application.properties         # Primary Spring Boot configuration
            ├── application-mysql.properties   # Configuration profile for MySQL Server & Workbench
            ├── application-dev.properties     # Embedded MySQL-compatible profile for zero-config run
            └── static/                        # Frontend web application
                ├── index.html                 # Entry point & automatic redirector
                ├── login.html                 # Admin login portal
                ├── dashboard.html             # Executive KPI & analytical overview
                ├── add-noise.html             # Manual data entry & CSV uploader
                ├── records.html               # Records directory with filters & edit/delete modals
                ├── map.html                   # Interactive Leaflet.js spatial city map
                ├── reports.html               # Compliance audit reports & threshold governance
                ├── css/
                │   └── style.css              # Cyber-clean dark theme & glassmorphism stylesheet
                └── js/
                    ├── api.js                 # Central REST API client helper
                    ├── auth.js                # Session management & route protection
                    ├── dashboard.js           # Dashboard KPI cards & Chart.js charts
                    ├── add-noise.js           # Live gauge preview & CSV upload logic
                    ├── records.js             # Table filters, inline edit, and CSV export
                    ├── map.js                 # Leaflet spatial map markers & popups
                    └── reports.js             # Analytical reports & threshold configuration
```

---

## 12. Academic Project Summary

- **Title**: Smart City Noise Pollution Spatial Mapping System and Compliance Tracking Dashboard
- **Domain**: Web Application Development / Smart City Environmental Informatics
- **Target Course**: 3rd Year Information Technology / Computer Science Mini Project
- **Outcome**: A production-grade, full-stack software application demonstrating complete MVC architecture, RESTful web services, dynamic compliance rule evaluation, spatial geospatial mapping, and automated dataset processing.
