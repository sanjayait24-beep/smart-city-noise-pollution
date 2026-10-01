package com.smartcity.noisemonitor.dto;

public class DashboardStatsDto {
    private long totalLocations;
    private long totalRecords;
    private long safeLocations;
    private long nonCompliantLocations;
    private double averageNoiseLevel;
    private double highestNoiseLevel;
    private String highestNoiseLocation;
    private double complianceRatePercent;

    public DashboardStatsDto() {}

    public DashboardStatsDto(long totalLocations, long totalRecords, long safeLocations,
                             long nonCompliantLocations, double averageNoiseLevel,
                             double highestNoiseLevel, String highestNoiseLocation,
                             double complianceRatePercent) {
        this.totalLocations = totalLocations;
        this.totalRecords = totalRecords;
        this.safeLocations = safeLocations;
        this.nonCompliantLocations = nonCompliantLocations;
        this.averageNoiseLevel = averageNoiseLevel;
        this.highestNoiseLevel = highestNoiseLevel;
        this.highestNoiseLocation = highestNoiseLocation;
        this.complianceRatePercent = complianceRatePercent;
    }

    public long getTotalLocations() { return totalLocations; }
    public void setTotalLocations(long totalLocations) { this.totalLocations = totalLocations; }

    public long getTotalRecords() { return totalRecords; }
    public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }

    public long getSafeLocations() { return safeLocations; }
    public void setSafeLocations(long safeLocations) { this.safeLocations = safeLocations; }

    public long getNonCompliantLocations() { return nonCompliantLocations; }
    public void setNonCompliantLocations(long nonCompliantLocations) { this.nonCompliantLocations = nonCompliantLocations; }

    public double getAverageNoiseLevel() { return averageNoiseLevel; }
    public void setAverageNoiseLevel(double averageNoiseLevel) { this.averageNoiseLevel = averageNoiseLevel; }

    public double getHighestNoiseLevel() { return highestNoiseLevel; }
    public void setHighestNoiseLevel(double highestNoiseLevel) { this.highestNoiseLevel = highestNoiseLevel; }

    public String getHighestNoiseLocation() { return highestNoiseLocation; }
    public void setHighestNoiseLocation(String highestNoiseLocation) { this.highestNoiseLocation = highestNoiseLocation; }

    public double getComplianceRatePercent() { return complianceRatePercent; }
    public void setComplianceRatePercent(double complianceRatePercent) { this.complianceRatePercent = complianceRatePercent; }
}
