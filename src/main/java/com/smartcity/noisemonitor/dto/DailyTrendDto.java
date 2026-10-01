package com.smartcity.noisemonitor.dto;

public class DailyTrendDto {
    private String date;
    private double averageNoise;
    private double maxNoise;
    private long recordCount;

    public DailyTrendDto() {}

    public DailyTrendDto(String date, double averageNoise, double maxNoise, long recordCount) {
        this.date = date;
        this.averageNoise = averageNoise;
        this.maxNoise = maxNoise;
        this.recordCount = recordCount;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getAverageNoise() { return averageNoise; }
    public void setAverageNoise(double averageNoise) { this.averageNoise = averageNoise; }

    public double getMaxNoise() { return maxNoise; }
    public void setMaxNoise(double maxNoise) { this.maxNoise = maxNoise; }

    public long getRecordCount() { return recordCount; }
    public void setRecordCount(long recordCount) { this.recordCount = recordCount; }
}
