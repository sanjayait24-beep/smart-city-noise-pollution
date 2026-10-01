package com.smartcity.noisemonitor.repository;

import com.smartcity.noisemonitor.model.ZoneLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZoneLimitRepository extends JpaRepository<ZoneLimit, Long> {
    Optional<ZoneLimit> findByZoneTypeIgnoreCase(String zoneType);
}
