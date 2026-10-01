package com.smartcity.noisemonitor.config;

import com.smartcity.noisemonitor.dto.NoiseRecordRequest;
import com.smartcity.noisemonitor.model.Admin;
import com.smartcity.noisemonitor.model.Location;
import com.smartcity.noisemonitor.model.ZoneLimit;
import com.smartcity.noisemonitor.repository.AdminRepository;
import com.smartcity.noisemonitor.repository.LocationRepository;
import com.smartcity.noisemonitor.repository.NoiseRecordRepository;
import com.smartcity.noisemonitor.repository.ZoneLimitRepository;
import com.smartcity.noisemonitor.service.NoiseRecordService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final ZoneLimitRepository zoneLimitRepository;
    private final LocationRepository locationRepository;
    private final NoiseRecordRepository noiseRecordRepository;
    private final NoiseRecordService noiseRecordService;

    public DataInitializer(AdminRepository adminRepository,
                           ZoneLimitRepository zoneLimitRepository,
                           LocationRepository locationRepository,
                           NoiseRecordRepository noiseRecordRepository,
                           NoiseRecordService noiseRecordService) {
        this.adminRepository = adminRepository;
        this.zoneLimitRepository = zoneLimitRepository;
        this.locationRepository = locationRepository;
        this.noiseRecordRepository = noiseRecordRepository;
        this.noiseRecordService = noiseRecordService;
    }

    @Override
    public void run(String... args) {
        // 1. Seed Admin
        if (adminRepository.count() == 0) {
            adminRepository.save(new Admin("admin", "admin123", "City Environmental Officer", "ADMIN"));
        }

        // 2. Seed Zone Limits
        if (zoneLimitRepository.count() == 0) {
            List<ZoneLimit> limits = Arrays.asList(
                    new ZoneLimit("Silence Zone", 50.0, 40.0, 50.0, "Hospital, educational institution, and court zones (100m radius)"),
                    new ZoneLimit("Residential", 55.0, 45.0, 55.0, "Areas designated predominantly for residential living"),
                    new ZoneLimit("Commercial", 65.0, 55.0, 65.0, "Retail, business districts, markets, and transit hubs"),
                    new ZoneLimit("Industrial", 75.0, 70.0, 75.0, "Manufacturing hubs and industrial development estates")
            );
            zoneLimitRepository.saveAll(limits);
        }

        // 3. Seed Monitoring Locations
        if (locationRepository.count() == 0) {
            List<Location> defaultLocations = Arrays.asList(
                    new Location("City General Hospital", "Silence Zone", 12.9716, 77.5946, "Sector 4, Healthcare Avenue"),
                    new Location("Green Valley Public School", "Silence Zone", 12.9650, 77.5850, "Ring Road East, Academic Enclave"),
                    new Location("Sunshine Palms Residency", "Residential", 12.9352, 77.6245, "Block B, 5th Main, Green Park"),
                    new Location("Lakeside Garden Apartments", "Residential", 12.9550, 77.6400, "Lake Promenade, Zone 2"),
                    new Location("Central Grand Mall & Station", "Commercial", 12.9780, 77.6010, "MG Road Junction, Central CBD"),
                    new Location("Tech Park Boulevard", "Commercial", 12.9260, 77.6830, "Silicon Corridor, Outer Ring Road"),
                    new Location("West Heavy Industrial Estate", "Industrial", 13.0300, 77.5200, "Phase 1, Manufacturing Belt"),
                    new Location("Auto Ancillary Complex", "Industrial", 13.0450, 77.5100, "Expressway Exit 7, Industrial Corridor")
            );
            locationRepository.saveAll(defaultLocations);
        }

        // 4. Seed Initial Noise Records
        if (noiseRecordRepository.count() == 0) {
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate twoDaysAgo = today.minusDays(2);

            seedRecord("City General Hospital", "Silence Zone", 46.5, twoDaysAgo.toString(), "09:30", 12.9716, 77.5946, "Routine morning monitoring outside OPD block. Ambient levels safe.");
            seedRecord("City General Hospital", "Silence Zone", 62.8, yesterday.toString(), "14:15", 12.9716, 77.5946, "Traffic honking and nearby road work caused threshold violation.");
            seedRecord("Green Valley Public School", "Silence Zone", 48.0, today.toString(), "11:00", 12.9650, 77.5850, "School hours acoustic check. Normal compliant sound level.");
            seedRecord("Sunshine Palms Residency", "Residential", 52.3, twoDaysAgo.toString(), "08:00", 12.9352, 77.6245, "Residential morning quiet hours check. Within safe limit.");
            seedRecord("Sunshine Palms Residency", "Residential", 68.4, yesterday.toString(), "20:45", 12.9352, 77.6245, "Commercial loudspeaker event violation in adjacent lot.");
            seedRecord("Lakeside Garden Apartments", "Residential", 54.1, today.toString(), "10:30", 12.9550, 77.6400, "Normal neighborhood acoustic footprint.");
            seedRecord("Central Grand Mall & Station", "Commercial", 63.5, twoDaysAgo.toString(), "12:00", 12.9780, 77.6010, "Mid-day transit shoppers flow. Compliant with commercial ceiling.");
            seedRecord("Central Grand Mall & Station", "Commercial", 78.2, yesterday.toString(), "18:30", 12.9780, 77.6010, "Peak evening traffic and generator noise violation.");
            seedRecord("Tech Park Boulevard", "Commercial", 61.0, today.toString(), "15:00", 12.9260, 77.6830, "Tech campus perimeter acoustic monitoring.");
            seedRecord("West Heavy Industrial Estate", "Industrial", 73.5, twoDaysAgo.toString(), "11:30", 13.0300, 77.5200, "Day shift operations within standard industrial limit.");
            seedRecord("West Heavy Industrial Estate", "Industrial", 84.0, yesterday.toString(), "16:00", 13.0300, 77.5200, "Unshielded metal stamping press caused severe spike.");
            seedRecord("Auto Ancillary Complex", "Industrial", 71.8, today.toString(), "13:45", 13.0450, 77.5100, "Component assembly lines within permissible range.");
        }
    }

    private void seedRecord(String locName, String zone, double noise, String date, String time, Double lat, Double lng, String desc) {
        NoiseRecordRequest req = new NoiseRecordRequest();
        req.setLocationName(locName);
        req.setZoneType(zone);
        req.setNoiseLevel(noise);
        req.setRecordDate(date);
        req.setRecordTime(time);
        req.setLatitude(lat);
        req.setLongitude(lng);
        req.setDescription(desc);
        noiseRecordService.createRecord(req);
    }
}
