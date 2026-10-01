package com.smartcity.noisemonitor.dto;

import jakarta.validation.constraints.*;

public class NoiseRecordRequest {

    @NotBlank(message = "Location name is required")
    private String locationName;

    @NotBlank(message = "Zone type is required")
    private String zoneType;

    @NotNull(message = "Noise level is required")
    @DecimalMin(value = "20.0", message = "Noise level must be at least 20.0 dB")
    @DecimalMax(value = "150.0", message = "Noise level cannot exceed 150.0 dB")
    private Double noiseLevel;

    @NotBlank(message = "Date is required (YYYY-MM-DD)")
    private String recordDate;

    @NotBlank(message = "Time is required (HH:mm)")
    private String recordTime;

    private Double latitude;
    private Double longitude;
    private String description;

    public NoiseRecordRequest() {}

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }

    public Double getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(Double noiseLevel) { this.noiseLevel = noiseLevel; }

    public String getRecordDate() { return recordDate; }
    public void setRecordDate(String recordDate) { this.recordDate = recordDate; }

    public String getRecordTime() { return recordTime; }
    public void setRecordTime(String recordTime) { this.recordTime = recordTime; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
