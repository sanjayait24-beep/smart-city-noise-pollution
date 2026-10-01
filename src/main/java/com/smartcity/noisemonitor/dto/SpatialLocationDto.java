package com.smartcity.noisemonitor.dto;

public class SpatialLocationDto {
    private Long id;
    private String name;
    private String zoneType;
    private Double latitude;
    private Double longitude;
    private String address;
    private Double latestNoiseLevel;
    private String latestComplianceStatus; // "SAFE" or "NON_COMPLIANT"
    private Double latestExceedance;
    private Double limitApplied;
    private String lastRecordedDate;
    private String lastRecordedTime;
    private int totalReadings;

    public SpatialLocationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatestNoiseLevel() { return latestNoiseLevel; }
    public void setLatestNoiseLevel(Double latestNoiseLevel) { this.latestNoiseLevel = latestNoiseLevel; }

    public String getLatestComplianceStatus() { return latestComplianceStatus; }
    public void setLatestComplianceStatus(String latestComplianceStatus) { this.latestComplianceStatus = latestComplianceStatus; }

    public Double getLatestExceedance() { return latestExceedance; }
    public void setLatestExceedance(Double latestExceedance) { this.latestExceedance = latestExceedance; }

    public Double getLimitApplied() { return limitApplied; }
    public void setLimitApplied(Double limitApplied) { this.limitApplied = limitApplied; }

    public String getLastRecordedDate() { return lastRecordedDate; }
    public void setLastRecordedDate(String lastRecordedDate) { this.lastRecordedDate = lastRecordedDate; }

    public String getLastRecordedTime() { return lastRecordedTime; }
    public void setLastRecordedTime(String lastRecordedTime) { this.lastRecordedTime = lastRecordedTime; }

    public int getTotalReadings() { return totalReadings; }
    public void setTotalReadings(int totalReadings) { this.totalReadings = totalReadings; }
}
