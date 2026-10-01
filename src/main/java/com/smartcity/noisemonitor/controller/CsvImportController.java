package com.smartcity.noisemonitor.controller;

import com.smartcity.noisemonitor.dto.ApiResponse;
import com.smartcity.noisemonitor.service.CsvImportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/csv")
@CrossOrigin(origins = "*")
public class CsvImportController {

    private final CsvImportService csvImportService;

    public CsvImportController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<CsvImportService.ImportResult>> importCsv(@RequestParam("file") MultipartFile file) {
        try {
            CsvImportService.ImportResult result = csvImportService.importCsv(file);
            String msg = String.format("CSV Processed: %d records imported, %d skipped",
                    result.getImportedCount(), result.getSkippedCount());
            return ResponseEntity.ok(ApiResponse.ok(msg, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("CSV import failed: " + e.getMessage()));
        }
    }

    @GetMapping("/sample-download")
    public ResponseEntity<String> getSampleCsv() {
        String csvContent = "location_name,zone_type,date,time,noise_level,latitude,longitude,description\n" +
                "Central Civic Hospital,Silence Zone,2026-09-18,10:30,45.5,12.9716,77.5946,Normal morning check outside OPD\n" +
                "Greenwood Enclave,Residential,2026-09-18,14:00,52.0,12.9352,77.6245,Quiet residential neighborhood inspection\n" +
                "Downtown Metro Plaza,Commercial,2026-09-18,17:45,74.5,12.9780,77.6010,High traffic evening transit junction\n" +
                "Apex Precision Industrial Park,Industrial,2026-09-18,11:15,73.0,13.0300,77.5200,Manufacturing unit daylight shift\n";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample_noise_data.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvContent);
    }
}
