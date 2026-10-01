package com.smartcity.noisemonitor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "zone_limits")
public class ZoneLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "zone_type", nullable = false, unique = true, length = 50)
    private String zoneType;

    @Column(name = "day_limit_db", nullable = false)
    private Double dayLimitDb;

    @Column(name = "night_limit_db", nullable = false)
    private Double nightLimitDb;

    @Column(name = "standard_limit_db", nullable = false)
    private Double standardLimitDb;

    @Column(length = 255)
    private String description;

    public ZoneLimit() {}

    public ZoneLimit(String zoneType, Double dayLimitDb, Double nightLimitDb, Double standardLimitDb, String description) {
        this.zoneType = zoneType;
        this.dayLimitDb = dayLimitDb;
        this.nightLimitDb = nightLimitDb;
        this.standardLimitDb = standardLimitDb;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }

    public Double getDayLimitDb() { return dayLimitDb; }
    public void setDayLimitDb(Double dayLimitDb) { this.dayLimitDb = dayLimitDb; }

    public Double getNightLimitDb() { return nightLimitDb; }
    public void setNightLimitDb(Double nightLimitDb) { this.nightLimitDb = nightLimitDb; }

    public Double getStandardLimitDb() { return standardLimitDb; }
    public void setStandardLimitDb(Double standardLimitDb) { this.standardLimitDb = standardLimitDb; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
