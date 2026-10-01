package com.smartcity.noisemonitor.repository;

import com.smartcity.noisemonitor.model.NoiseRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoiseRecordRepository extends JpaRepository<NoiseRecord, Long> {

    List<NoiseRecord> findTop10ByOrderByRecordDateDescRecordTimeDesc();

    List<NoiseRecord> findByLocationIdOrderByRecordDateDescRecordTimeDesc(Long locationId);

    Optional<NoiseRecord> findFirstByLocationIdOrderByRecordDateDescRecordTimeDesc(Long locationId);

    Optional<NoiseRecord> findTopByOrderByNoiseLevelDesc();

    long countByComplianceStatusIgnoreCase(String complianceStatus);

    @Query("SELECT AVG(n.noiseLevel) FROM NoiseRecord n")
    Double calculateOverallAverageNoise();

    @Query("SELECT MAX(n.noiseLevel) FROM NoiseRecord n")
    Double calculateOverallHighestNoise();

    @Query("SELECT n FROM NoiseRecord n WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(n.locationName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(n.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:zoneType IS NULL OR :zoneType = '' OR LOWER(n.zoneType) = LOWER(:zoneType)) AND " +
           "(:complianceStatus IS NULL OR :complianceStatus = '' OR LOWER(n.complianceStatus) = LOWER(:complianceStatus)) AND " +
           "(:startDate IS NULL OR n.recordDate >= :startDate) AND " +
           "(:endDate IS NULL OR n.recordDate <= :endDate) " +
           "ORDER BY n.recordDate DESC, n.recordTime DESC")
    List<NoiseRecord> searchAndFilter(
            @Param("search") String search,
            @Param("zoneType") String zoneType,
            @Param("complianceStatus") String complianceStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT n FROM NoiseRecord n WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(n.locationName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(n.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:zoneType IS NULL OR :zoneType = '' OR LOWER(n.zoneType) = LOWER(:zoneType)) AND " +
           "(:complianceStatus IS NULL OR :complianceStatus = '' OR LOWER(n.complianceStatus) = LOWER(:complianceStatus)) AND " +
           "(:startDate IS NULL OR n.recordDate >= :startDate) AND " +
           "(:endDate IS NULL OR n.recordDate <= :endDate)")
    Page<NoiseRecord> searchAndFilterPaged(
            @Param("search") String search,
            @Param("zoneType") String zoneType,
            @Param("complianceStatus") String complianceStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT n.recordDate, AVG(n.noiseLevel), MAX(n.noiseLevel), COUNT(n) FROM NoiseRecord n GROUP BY n.recordDate ORDER BY n.recordDate ASC")
    List<Object[]> findDailyNoiseAverages();

    @Query("SELECT n.zoneType, AVG(n.noiseLevel), COUNT(n), " +
           "SUM(CASE WHEN LOWER(n.complianceStatus) = 'safe' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN LOWER(n.complianceStatus) <> 'safe' THEN 1 ELSE 0 END) " +
           "FROM NoiseRecord n GROUP BY n.zoneType")
    List<Object[]> findZoneSummaries();

    List<NoiseRecord> findTop5ByOrderByNoiseLevelDesc();
}
