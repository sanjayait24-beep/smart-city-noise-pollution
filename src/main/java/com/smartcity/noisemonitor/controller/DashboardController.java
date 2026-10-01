package com.smartcity.noisemonitor.controller;

import com.smartcity.noisemonitor.dto.ApiResponse;
import com.smartcity.noisemonitor.dto.DailyTrendDto;
import com.smartcity.noisemonitor.dto.DashboardStatsDto;
import com.smartcity.noisemonitor.dto.ZoneSummaryDto;
import com.smartcity.noisemonitor.model.NoiseRecord;
import com.smartcity.noisemonitor.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/zone-summary")
    public ResponseEntity<ApiResponse<List<ZoneSummaryDto>>> getZoneSummary() {
        List<ZoneSummaryDto> summaries = dashboardService.getZoneSummaries();
        return ResponseEntity.ok(ApiResponse.ok(summaries));
    }

    @GetMapping("/daily-trends")
    public ResponseEntity<ApiResponse<List<DailyTrendDto>>> getDailyTrends() {
        List<DailyTrendDto> trends = dashboardService.getDailyTrends();
        return ResponseEntity.ok(ApiResponse.ok(trends));
    }

    @GetMapping("/top-violations")
    public ResponseEntity<ApiResponse<List<NoiseRecord>>> getTopViolations() {
        List<NoiseRecord> violations = dashboardService.getTopViolations();
        return ResponseEntity.ok(ApiResponse.ok(violations));
    }
}
