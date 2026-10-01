package com.smartcity.noisemonitor.repository;

import com.smartcity.noisemonitor.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByNameIgnoreCase(String name);
    List<Location> findByZoneTypeIgnoreCase(String zoneType);
}
