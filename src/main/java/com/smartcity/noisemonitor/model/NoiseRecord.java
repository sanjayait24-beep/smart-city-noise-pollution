package com.smartcity.noisemonitor.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "noise_records")
public class NoiseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = true)
    private Location location;

    @Column(name = "location_name", nullable = false, length = 100)
    private String locationName;

    @Column(name = "city", length = 100)
    private String city = "Bengaluru";

    @Column(name = "zone_type", nullable = false, length = 50)
    private String zoneType;

    @Column(name = "noise_level", nullable = false)
    private Double noiseLevel;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "record_time", nullable = false)
    private LocalTime recordTime;

    @Column(name = "limit_applied", nullable = false)
    private Double limitApplied;

    @Column(name = "exceedance_db", nullable = false)
    private Double exceedanceDb;

    @Column(name = "compliance_status", nullable = false, length = 20)
    private String complianceStatus; // "SAFE" or "NON_COMPLIANT"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public NoiseRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }

    public Double getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(Double noiseLevel) { this.noiseLevel = noiseLevel; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public LocalTime getRecordTime() { return recordTime; }
    public void setRecordTime(LocalTime recordTime) { this.recordTime = recordTime; }

    public Double getLimitApplied() { return limitApplied; }
    public void setLimitApplied(Double limitApplied) { this.limitApplied = limitApplied; }

    public Double getExceedanceDb() { return exceedanceDb; }
    public void setExceedanceDb(Double exceedanceDb) { this.exceedanceDb = exceedanceDb; }

    public String getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
