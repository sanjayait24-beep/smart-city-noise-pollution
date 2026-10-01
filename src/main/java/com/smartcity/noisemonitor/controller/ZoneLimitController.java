package com.smartcity.noisemonitor.controller;

import com.smartcity.noisemonitor.dto.ApiResponse;
import com.smartcity.noisemonitor.model.ZoneLimit;
import com.smartcity.noisemonitor.service.ZoneLimitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zone-limits")
@CrossOrigin(origins = "*")
public class ZoneLimitController {

    private final ZoneLimitService zoneLimitService;

    public ZoneLimitController(ZoneLimitService zoneLimitService) {
        this.zoneLimitService = zoneLimitService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ZoneLimit>>> getAllLimits() {
        return ResponseEntity.ok(ApiResponse.ok(zoneLimitService.getAllLimits()));
    }

    @GetMapping("/{zoneType}")
    public ResponseEntity<ApiResponse<ZoneLimit>> getLimitByZone(@PathVariable String zoneType) {
        return zoneLimitService.getLimitByZone(zoneType)
                .map(zl -> ResponseEntity.ok(ApiResponse.ok(zl)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneLimit>> updateLimit(@PathVariable Long id, @RequestBody ZoneLimit updated) {
        try {
            ZoneLimit saved = zoneLimitService.updateLimit(id, updated);
            return ResponseEntity.ok(ApiResponse.ok("Zone limit updated successfully", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
