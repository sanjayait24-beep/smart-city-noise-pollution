package com.smartcity.noisemonitor.controller;

import com.smartcity.noisemonitor.dto.ApiResponse;
import com.smartcity.noisemonitor.dto.SpatialLocationDto;
import com.smartcity.noisemonitor.model.Location;
import com.smartcity.noisemonitor.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Location>>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity.ok(ApiResponse.ok(locations));
    }

    @GetMapping("/spatial")
    public ResponseEntity<ApiResponse<List<SpatialLocationDto>>> getSpatialLocations() {
        List<SpatialLocationDto> spatialLocations = locationService.getSpatialLocations();
        return ResponseEntity.ok(ApiResponse.ok(spatialLocations));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Location>> createLocation(@RequestBody Location location) {
        if (location.getName() == null || location.getName().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Location name is required"));
        }
        Location saved = locationService.saveLocation(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Location created", saved));
    }
}
