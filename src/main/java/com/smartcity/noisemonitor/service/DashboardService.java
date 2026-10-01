package com.smartcity.noisemonitor.service;

import com.smartcity.noisemonitor.dto.DailyTrendDto;
import com.smartcity.noisemonitor.dto.DashboardStatsDto;
import com.smartcity.noisemonitor.dto.SpatialLocationDto;
import com.smartcity.noisemonitor.dto.ZoneSummaryDto;
import com.smartcity.noisemonitor.model.NoiseRecord;
import com.smartcity.noisemonitor.model.ZoneLimit;
import com.smartcity.noisemonitor.repository.LocationRepository;
import com.smartcity.noisemonitor.repository.NoiseRecordRepository;
import com.smartcity.noisemonitor.repository.ZoneLimitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DashboardService {

    private final NoiseRecordRepository noiseRecordRepository;
    private final LocationRepository locationRepository;
    private final LocationService locationService;
    private final ZoneLimitRepository zoneLimitRepository;

    public DashboardService(NoiseRecordRepository noiseRecordRepository,
                            LocationRepository locationRepository,
                            LocationService locationService,
                            ZoneLimitRepository zoneLimitRepository) {
        this.noiseRecordRepository = noiseRecordRepository;
        this.locationRepository = locationRepository;
        this.locationService = locationService;
        this.zoneLimitRepository = zoneLimitRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        long totalLocations = locationRepository.count();
        long totalRecords = noiseRecordRepository.count();

        // Calculate safe vs non-compliant locations based on spatial latest status
        List<SpatialLocationDto> spatialLocations = locationService.getSpatialLocations();
        long safeLocations = 0;
        long nonCompliantLocations = 0;

        for (SpatialLocationDto loc : spatialLocations) {
            if ("SAFE".equalsIgnoreCase(loc.getLatestComplianceStatus())) {
                safeLocations++;
            } else if ("NON_COMPLIANT".equalsIgnoreCase(loc.getLatestComplianceStatus())) {
                nonCompliantLocations++;
            }
        }

        Double avgDb = noiseRecordRepository.calculateOverallAverageNoise();
        double averageNoise = (avgDb != null) ? Math.round(avgDb * 10.0) / 10.0 : 0.0;

        Double maxDb = noiseRecordRepository.calculateOverallHighestNoise();
        double highestNoise = (maxDb != null) ? maxDb : 0.0;

        String highestLocationName = "None";
        Optional<NoiseRecord> topRecord = noiseRecordRepository.findTopByOrderByNoiseLevelDesc();
        if (topRecord.isPresent()) {
            highestLocationName = topRecord.get().getLocationName() + " (" + topRecord.get().getNoiseLevel() + " dB)";
        }

        long safeRecordsCount = noiseRecordRepository.countByComplianceStatusIgnoreCase("SAFE");
        double complianceRate = (totalRecords > 0) ? Math.round(((double) safeRecordsCount / totalRecords * 100.0) * 10.0) / 10.0 : 100.0;

        return new DashboardStatsDto(
                totalLocations,
                totalRecords,
                safeLocations,
                nonCompliantLocations,
                averageNoise,
                highestNoise,
                highestLocationName,
                complianceRate
        );
    }

    public List<ZoneSummaryDto> getZoneSummaries() {
        List<Object[]> rawList = noiseRecordRepository.findZoneSummaries();
        Map<String, ZoneSummaryDto> summaryMap = new LinkedHashMap<>();

        // Initialize with default 4 standard zones
        for (ZoneLimit zl : zoneLimitRepository.findAll()) {
            summaryMap.put(zl.getZoneType().toLowerCase(), new ZoneSummaryDto(
                    zl.getZoneType(),
                    0,
                    0.0,
                    0,
                    0,
                    zl.getDayLimitDb(),
                    zl.getNightLimitDb(),
                    zl.getStandardLimitDb()
            ));
        }

        for (Object[] row : rawList) {
            String zoneType = (String) row[0];
            Double avgNoise = (Double) row[1];
            long count = ((Number) row[2]).longValue();
            long safeCount = (row[3] != null) ? ((Number) row[3]).longValue() : 0;
            long nonSafeCount = (row[4] != null) ? ((Number) row[4]).longValue() : 0;

            String key = zoneType.toLowerCase();
            ZoneSummaryDto dto = summaryMap.get(key);
            if (dto == null) {
                dto = new ZoneSummaryDto(zoneType, count, (avgNoise != null) ? Math.round(avgNoise * 10.0) / 10.0 : 0.0,
                        safeCount, nonSafeCount, 60.0, 50.0, 55.0);
            } else {
                dto.setRecordCount(count);
                dto.setAverageNoise((avgNoise != null) ? Math.round(avgNoise * 10.0) / 10.0 : 0.0);
                dto.setSafeCount(safeCount);
                dto.setNonCompliantCount(nonSafeCount);
            }
            summaryMap.put(key, dto);
        }

        return new ArrayList<>(summaryMap.values());
    }

    public List<DailyTrendDto> getDailyTrends() {
        List<Object[]> rawList = noiseRecordRepository.findDailyNoiseAverages();
        List<DailyTrendDto> trends = new ArrayList<>();

        for (Object[] row : rawList) {
            LocalDate date = (LocalDate) row[0];
            Double avg = (Double) row[1];
            Double max = (Double) row[2];
            long count = ((Number) row[3]).longValue();

            trends.add(new DailyTrendDto(
                    date.toString(),
                    (avg != null) ? Math.round(avg * 10.0) / 10.0 : 0.0,
                    (max != null) ? max : 0.0,
                    count
            ));
        }

        return trends;
    }

    public List<NoiseRecord> getTopViolations() {
        return noiseRecordRepository.findTop5ByOrderByNoiseLevelDesc();
    }
}
