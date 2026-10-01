package com.smartcity.noisemonitor.dto;

public class ZoneSummaryDto {
    private String zoneType;
    private long recordCount;
    private double averageNoise;
    private long safeCount;
    private long nonCompliantCount;
    private double dayLimit;
    private double nightLimit;
    private double standardLimit;

    public ZoneSummaryDto() {}

    public ZoneSummaryDto(String zoneType, long recordCount, double averageNoise,
                          long safeCount, long nonCompliantCount,
                          double dayLimit, double nightLimit, double standardLimit) {
        this.zoneType = zoneType;
        this.recordCount = recordCount;
        this.averageNoise = averageNoise;
        this.safeCount = safeCount;
        this.nonCompliantCount = nonCompliantCount;
        this.dayLimit = dayLimit;
        this.nightLimit = nightLimit;
        this.standardLimit = standardLimit;
    }

    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }

    public long getRecordCount() { return recordCount; }
    public void setRecordCount(long recordCount) { this.recordCount = recordCount; }

    public double getAverageNoise() { return averageNoise; }
    public void setAverageNoise(double averageNoise) { this.averageNoise = averageNoise; }

    public long getSafeCount() { return safeCount; }
    public void setSafeCount(long safeCount) { this.safeCount = safeCount; }

    public long getNonCompliantCount() { return nonCompliantCount; }
    public void setNonCompliantCount(long nonCompliantCount) { this.nonCompliantCount = nonCompliantCount; }

    public double getDayLimit() { return dayLimit; }
    public void setDayLimit(double dayLimit) { this.dayLimit = dayLimit; }

    public double getNightLimit() { return nightLimit; }
    public void setNightLimit(double nightLimit) { this.nightLimit = nightLimit; }

    public double getStandardLimit() { return standardLimit; }
    public void setStandardLimit(double standardLimit) { this.standardLimit = standardLimit; }
}
