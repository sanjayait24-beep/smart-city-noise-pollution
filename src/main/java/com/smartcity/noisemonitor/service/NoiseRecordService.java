package com.smartcity.noisemonitor.service;

import com.smartcity.noisemonitor.dto.NoiseRecordRequest;
import com.smartcity.noisemonitor.model.Location;
import com.smartcity.noisemonitor.model.NoiseRecord;
import com.smartcity.noisemonitor.repository.NoiseRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class NoiseRecordService {

    private final NoiseRecordRepository noiseRecordRepository;
    private final LocationService locationService;
    private final ComplianceService complianceService;

    public NoiseRecordService(NoiseRecordRepository noiseRecordRepository,
                              LocationService locationService,
                              ComplianceService complianceService) {
        this.noiseRecordRepository = noiseRecordRepository;
        this.locationService = locationService;
        this.complianceService = complianceService;
    }

    public List<NoiseRecord> getAllRecords() {
        return noiseRecordRepository.findAll();
    }

    public Optional<NoiseRecord> getRecordById(Long id) {
        return noiseRecordRepository.findById(id);
    }

    public List<NoiseRecord> searchAndFilter(String search, String zoneType, String complianceStatus, String startDateStr, String endDateStr) {
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (startDateStr != null && !startDateStr.isBlank()) {
            try { startDate = LocalDate.parse(startDateStr.trim()); } catch (Exception ignored) {}
        }
        if (endDateStr != null && !endDateStr.isBlank()) {
            try { endDate = LocalDate.parse(endDateStr.trim()); } catch (Exception ignored) {}
        }

        return noiseRecordRepository.searchAndFilter(
                (search != null && !search.isBlank()) ? search.trim() : null,
                (zoneType != null && !zoneType.isBlank()) ? zoneType.trim() : null,
                (complianceStatus != null && !complianceStatus.isBlank()) ? complianceStatus.trim() : null,
                startDate,
                endDate);
    }

    public NoiseRecord createRecord(NoiseRecordRequest request) {
        LocalDate recordDate = (request.getRecordDate() != null && !request.getRecordDate().isBlank())
                ? LocalDate.parse(request.getRecordDate().trim())
                : LocalDate.now();

        LocalTime recordTime;
        if (request.getRecordTime() != null && !request.getRecordTime().isBlank()) {
            String timeStr = request.getRecordTime().trim();
            if (timeStr.length() == 5) {
                timeStr += ":00";
            }
            recordTime = LocalTime.parse(timeStr);
        } else {
            recordTime = LocalTime.now();
        }

        // Link or create location
        Location location = locationService.getOrCreateLocation(
                request.getLocationName(),
                request.getZoneType(),
                request.getLatitude(),
                request.getLongitude(),
                request.getDescription());

        // Dynamic compliance evaluation
        ComplianceService.ComplianceResult comp = complianceService.evaluateCompliance(
                request.getZoneType(),
                request.getNoiseLevel(),
                recordTime);

        NoiseRecord record = new NoiseRecord();
        record.setLocation(location);
        record.setLocationName(location.getName());
        record.setZoneType(request.getZoneType());
        record.setNoiseLevel(request.getNoiseLevel());
        record.setRecordDate(recordDate);
        record.setRecordTime(recordTime);
        record.setLimitApplied(comp.getLimitApplied());
        record.setExceedanceDb(comp.getExceedanceDb());
        record.setComplianceStatus(comp.getComplianceStatus());
        record.setDescription(request.getDescription());

        return noiseRecordRepository.save(record);
    }

    public NoiseRecord updateRecord(Long id, NoiseRecordRequest request) {
        NoiseRecord record = noiseRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with ID: " + id));

        LocalDate recordDate = (request.getRecordDate() != null && !request.getRecordDate().isBlank())
                ? LocalDate.parse(request.getRecordDate().trim())
                : record.getRecordDate();

        LocalTime recordTime = record.getRecordTime();
        if (request.getRecordTime() != null && !request.getRecordTime().isBlank()) {
            String timeStr = request.getRecordTime().trim();
            if (timeStr.length() == 5) timeStr += ":00";
            recordTime = LocalTime.parse(timeStr);
        }

        if (request.getLocationName() != null && !request.getLocationName().isBlank()) {
            record.setLocationName(request.getLocationName().trim());
        }
        if (request.getZoneType() != null && !request.getZoneType().isBlank()) {
            record.setZoneType(request.getZoneType().trim());
        }
        if (request.getNoiseLevel() != null) {
            record.setNoiseLevel(request.getNoiseLevel());
        }
        if (request.getDescription() != null) {
            record.setDescription(request.getDescription().trim());
        }

        record.setRecordDate(recordDate);
        record.setRecordTime(recordTime);

        // Re-evaluate compliance based on updated values
        ComplianceService.ComplianceResult comp = complianceService.evaluateCompliance(
                record.getZoneType(),
                record.getNoiseLevel(),
                record.getRecordTime());

        record.setLimitApplied(comp.getLimitApplied());
        record.setExceedanceDb(comp.getExceedanceDb());
        record.setComplianceStatus(comp.getComplianceStatus());

        return noiseRecordRepository.save(record);
    }

    public void deleteRecord(Long id) {
        if (!noiseRecordRepository.existsById(id)) {
            throw new IllegalArgumentException("Record not found with ID: " + id);
        }
        noiseRecordRepository.deleteById(id);
    }

    public List<NoiseRecord> getRecentRecords() {
        return noiseRecordRepository.findTop10ByOrderByRecordDateDescRecordTimeDesc();
    }
}
