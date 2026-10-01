package com.smartcity.noisemonitor.controller;

import com.smartcity.noisemonitor.dto.ApiResponse;
import com.smartcity.noisemonitor.dto.NoiseRecordRequest;
import com.smartcity.noisemonitor.model.NoiseRecord;
import com.smartcity.noisemonitor.service.NoiseRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/noise-records")
@CrossOrigin(origins = "*")
public class NoiseRecordController {

    private final NoiseRecordService noiseRecordService;

    public NoiseRecordController(NoiseRecordService noiseRecordService) {
        this.noiseRecordService = noiseRecordService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoiseRecord>>> getRecords(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String zoneType,
            @RequestParam(required = false) String complianceStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<NoiseRecord> records = noiseRecordService.searchAndFilter(search, zoneType, complianceStatus, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(records));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<NoiseRecord>>> getRecentRecords() {
        List<NoiseRecord> records = noiseRecordService.getRecentRecords();
        return ResponseEntity.ok(ApiResponse.ok(records));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoiseRecord>> getRecordById(@PathVariable Long id) {
        return noiseRecordService.getRecordById(id)
                .map(r -> ResponseEntity.ok(ApiResponse.ok(r)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Record not found with ID: " + id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NoiseRecord>> createRecord(@Valid @RequestBody NoiseRecordRequest request) {
        try {
            NoiseRecord created = noiseRecordService.createRecord(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Noise record saved and compliance evaluated", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to save noise record: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NoiseRecord>> updateRecord(@PathVariable Long id, @Valid @RequestBody NoiseRecordRequest request) {
        try {
            NoiseRecord updated = noiseRecordService.updateRecord(id, request);
            return ResponseEntity.ok(ApiResponse.ok("Noise record updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update record: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        try {
            noiseRecordService.deleteRecord(id);
            return ResponseEntity.ok(ApiResponse.ok("Noise record deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }
}
