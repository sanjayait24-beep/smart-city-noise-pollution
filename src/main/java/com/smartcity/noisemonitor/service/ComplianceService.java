package com.smartcity.noisemonitor.service;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class ComplianceService {

    private final ZoneLimitService zoneLimitService;

    public ComplianceService(ZoneLimitService zoneLimitService) {
        this.zoneLimitService = zoneLimitService;
    }

    public static class ComplianceResult {
        private final double limitApplied;
        private final double exceedanceDb;
        private final String complianceStatus; // "SAFE" or "NON_COMPLIANT"
        private final boolean compliant;

        public ComplianceResult(double limitApplied, double exceedanceDb, String complianceStatus, boolean compliant) {
            this.limitApplied = limitApplied;
            this.exceedanceDb = exceedanceDb;
            this.complianceStatus = complianceStatus;
            this.compliant = compliant;
        }

        public double getLimitApplied() { return limitApplied; }
        public double getExceedanceDb() { return exceedanceDb; }
        public String getComplianceStatus() { return complianceStatus; }
        public boolean isCompliant() { return compliant; }
    }

    /**
     * Evaluates compliance against dynamic zone limits
     */
    public ComplianceResult evaluateCompliance(String zoneType, double noiseLevel, LocalTime time) {
        double limit = zoneLimitService.resolveLimit(zoneType, time);
        double exceedance = Math.max(0.0, Math.round((noiseLevel - limit) * 100.0) / 100.0);
        boolean isCompliant = noiseLevel <= limit;
        String status = isCompliant ? "SAFE" : "NON_COMPLIANT";

        return new ComplianceResult(limit, exceedance, status, isCompliant);
    }
}
