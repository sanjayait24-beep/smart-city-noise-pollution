package com.smartcity.noisemonitor.service;

import com.smartcity.noisemonitor.model.ZoneLimit;
import com.smartcity.noisemonitor.repository.ZoneLimitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ZoneLimitService {

    private final ZoneLimitRepository zoneLimitRepository;

    public ZoneLimitService(ZoneLimitRepository zoneLimitRepository) {
        this.zoneLimitRepository = zoneLimitRepository;
    }

    public List<ZoneLimit> getAllLimits() {
        return zoneLimitRepository.findAll();
    }

    public Optional<ZoneLimit> getLimitByZone(String zoneType) {
        return zoneLimitRepository.findByZoneTypeIgnoreCase(zoneType.trim());
    }

    public ZoneLimit updateLimit(Long id, ZoneLimit updated) {
        ZoneLimit existing = zoneLimitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Zone limit not found with ID: " + id));
        existing.setDayLimitDb(updated.getDayLimitDb());
        existing.setNightLimitDb(updated.getNightLimitDb());
        existing.setStandardLimitDb(updated.getStandardLimitDb());
        existing.setDescription(updated.getDescription());
        return zoneLimitRepository.save(existing);
    }

    /**
     * Resolves the statutory limit for a zone and time.
     * Day time: 06:00:00 to 22:00:00.
     * Night time: 22:00:00 to 06:00:00.
     */
    public double resolveLimit(String zoneType, LocalTime time) {
        Optional<ZoneLimit> opt = zoneLimitRepository.findByZoneTypeIgnoreCase(zoneType.trim());
        if (opt.isPresent()) {
            ZoneLimit zl = opt.get();
            if (time != null) {
                boolean isDayTime = !time.isBefore(LocalTime.of(6, 0)) && time.isBefore(LocalTime.of(22, 0));
                return isDayTime ? zl.getDayLimitDb() : zl.getNightLimitDb();
            }
            return zl.getStandardLimitDb();
        }

        // Fallback standard CPCB standards if not found in table
        String normalized = zoneType.toUpperCase();
        if (normalized.contains("SILENCE")) return 50.0;
        if (normalized.contains("RESIDENTIAL")) return 55.0;
        if (normalized.contains("COMMERCIAL")) return 65.0;
        if (normalized.contains("INDUSTRIAL")) return 75.0;
        return 60.0;
    }
}
