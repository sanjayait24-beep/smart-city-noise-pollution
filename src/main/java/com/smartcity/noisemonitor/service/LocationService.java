package com.smartcity.noisemonitor.service;

import com.smartcity.noisemonitor.dto.SpatialLocationDto;
import com.smartcity.noisemonitor.model.Location;
import com.smartcity.noisemonitor.model.NoiseRecord;
import com.smartcity.noisemonitor.repository.LocationRepository;
import com.smartcity.noisemonitor.repository.NoiseRecordRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final NoiseRecordRepository noiseRecordRepository;

    public LocationService(LocationRepository locationRepository, NoiseRecordRepository noiseRecordRepository) {
        this.locationRepository = locationRepository;
        this.noiseRecordRepository = noiseRecordRepository;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Optional<Location> findById(Long id) {
        return locationRepository.findById(id);
    }

    public Optional<Location> findByName(String name) {
        return locationRepository.findByNameIgnoreCase(name.trim());
    }

    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location getOrCreateLocation(String name, String zoneType, Double lat, Double lng, String address) {
        Optional<Location> existing = locationRepository.findByNameIgnoreCase(name.trim());
        if (existing.isPresent()) {
            Location loc = existing.get();
            // Update coordinates if provided and not yet set
            if (lat != null && lng != null) {
                loc.setLatitude(lat);
                loc.setLongitude(lng);
            }
            if (zoneType != null && !zoneType.isBlank()) {
                loc.setZoneType(zoneType);
            }
            return locationRepository.save(loc);
        }

        // Create new location with defaults if coordinates are missing
        double defaultLat = (lat != null) ? lat : 12.9716;
        double defaultLng = (lng != null) ? lng : 77.5946;
        String defaultAddr = (address != null && !address.isBlank()) ? address : (name + ", Smart City Corridor");

        Location newLoc = new Location(name.trim(), zoneType, defaultLat, defaultLng, defaultAddr);
        return locationRepository.save(newLoc);
    }

    /**
     * Builds enriched spatial location DTOs for map rendering
     */
    public List<SpatialLocationDto> getSpatialLocations() {
        List<Location> locations = locationRepository.findAll();
        List<SpatialLocationDto> dtoList = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (Location loc : locations) {
            SpatialLocationDto dto = new SpatialLocationDto();
            dto.setId(loc.getId());
            dto.setName(loc.getName());
            dto.setZoneType(loc.getZoneType());
            dto.setLatitude(loc.getLatitude());
            dto.setLongitude(loc.getLongitude());
            dto.setAddress(loc.getAddress());

            // Query latest noise record for this location
            Optional<NoiseRecord> latestRecordOpt = noiseRecordRepository.findFirstByLocationIdOrderByRecordDateDescRecordTimeDesc(loc.getId());
            if (latestRecordOpt.isPresent()) {
                NoiseRecord nr = latestRecordOpt.get();
                dto.setLatestNoiseLevel(nr.getNoiseLevel());
                dto.setLatestComplianceStatus(nr.getComplianceStatus());
                dto.setLatestExceedance(nr.getExceedanceDb());
                dto.setLimitApplied(nr.getLimitApplied());
                dto.setLastRecordedDate(nr.getRecordDate().format(dateFormatter));
                dto.setLastRecordedTime(nr.getRecordTime().format(timeFormatter));
            } else {
                dto.setLatestNoiseLevel(0.0);
                dto.setLatestComplianceStatus("UNKNOWN");
                dto.setLatestExceedance(0.0);
                dto.setLimitApplied(0.0);
                dto.setLastRecordedDate("N/A");
                dto.setLastRecordedTime("N/A");
            }

            dto.setTotalReadings(noiseRecordRepository.findByLocationIdOrderByRecordDateDescRecordTimeDesc(loc.getId()).size());
            dtoList.add(dto);
        }

        return dtoList;
    }
}
