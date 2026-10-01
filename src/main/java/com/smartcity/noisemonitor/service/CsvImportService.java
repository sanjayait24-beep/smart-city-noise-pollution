package com.smartcity.noisemonitor.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.smartcity.noisemonitor.dto.NoiseRecordRequest;
import com.smartcity.noisemonitor.model.NoiseRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class CsvImportService {

    private final NoiseRecordService noiseRecordService;

    public CsvImportService(NoiseRecordService noiseRecordService) {
        this.noiseRecordService = noiseRecordService;
    }

    public static class ImportResult {
        private int totalRows;
        private int importedCount;
        private int skippedCount;
        private List<String> errors = new ArrayList<>();
        private List<NoiseRecord> importedRecords = new ArrayList<>();

        public int getTotalRows() { return totalRows; }
        public void setTotalRows(int totalRows) { this.totalRows = totalRows; }

        public int getImportedCount() { return importedCount; }
        public void setImportedCount(int importedCount) { this.importedCount = importedCount; }

        public int getSkippedCount() { return skippedCount; }
        public void setSkippedCount(int skippedCount) { this.skippedCount = skippedCount; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }

        public List<NoiseRecord> getImportedRecords() { return importedRecords; }
        public void setImportedRecords(List<NoiseRecord> importedRecords) { this.importedRecords = importedRecords; }
    }

    public ImportResult importCsv(MultipartFile file) throws Exception {
        ImportResult result = new ImportResult();
        if (file.isEmpty()) {
            result.getErrors().add("Uploaded file is empty");
            return result;
        }

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader).build()) {

            List<String[]> rows = csvReader.readAll();
            if (rows.isEmpty()) {
                result.getErrors().add("CSV file has no rows");
                return result;
            }

            // Detect header
            int startRow = 0;
            String[] firstRow = rows.get(0);
            if (firstRow.length > 0 && firstRow[0].toLowerCase().contains("location")) {
                startRow = 1; // skip header
            }

            result.setTotalRows(rows.size() - startRow);

            for (int i = startRow; i < rows.size(); i++) {
                String[] row = rows.get(i);
                int lineNum = i + 1;

                if (row.length < 5) {
                    result.getErrors().add("Row " + lineNum + ": Insufficient columns (expected at least 5 columns)");
                    result.setSkippedCount(result.getSkippedCount() + 1);
                    continue;
                }

                String locationName = row[0] != null ? row[0].trim() : "";
                String zoneType = row[1] != null ? row[1].trim() : "";
                String dateStr = row[2] != null ? row[2].trim() : "";
                String timeStr = row[3] != null ? row[3].trim() : "";
                String noiseStr = row[4] != null ? row[4].trim() : "";
                String latStr = (row.length > 5 && row[5] != null) ? row[5].trim() : "";
                String lngStr = (row.length > 6 && row[6] != null) ? row[6].trim() : "";
                String desc = (row.length > 7 && row[7] != null) ? row[7].trim() : "";

                // Validations
                if (locationName.isBlank()) {
                    result.getErrors().add("Row " + lineNum + ": Missing location name");
                    result.setSkippedCount(result.getSkippedCount() + 1);
                    continue;
                }

                if (zoneType.isBlank()) {
                    result.getErrors().add("Row " + lineNum + ": Missing zone type");
                    result.setSkippedCount(result.getSkippedCount() + 1);
                    continue;
                }

                double noiseLevel;
                try {
                    noiseLevel = Double.parseDouble(noiseStr);
                    if (noiseLevel < 20.0 || noiseLevel > 150.0) {
                        result.getErrors().add("Row " + lineNum + ": Noise level must be between 20 and 150 dB (found " + noiseStr + ")");
                        result.setSkippedCount(result.getSkippedCount() + 1);
                        continue;
                    }
                } catch (NumberFormatException e) {
                    result.getErrors().add("Row " + lineNum + ": Invalid noise level value: " + noiseStr);
                    result.setSkippedCount(result.getSkippedCount() + 1);
                    continue;
                }

                try {
                    LocalDate.parse(dateStr);
                } catch (Exception e) {
                    result.getErrors().add("Row " + lineNum + ": Invalid date format (expected YYYY-MM-DD): " + dateStr);
                    result.setSkippedCount(result.getSkippedCount() + 1);
                    continue;
                }

                try {
                    String cleanTime = timeStr.length() == 5 ? timeStr + ":00" : timeStr;
                    LocalTime.parse(cleanTime);
                } catch (Exception e) {
                    result.getErrors().add("Row " + lineNum + ": Invalid time format (expected HH:mm or HH:mm:ss): " + timeStr);
                    result.setSkippedCount(result.getSkippedCount() + 1);
                    continue;
                }

                Double lat = null;
                Double lng = null;
                if (!latStr.isBlank()) {
                    try {
                        lat = Double.parseDouble(latStr);
                        if (lat < -90.0 || lat > 90.0) throw new IllegalArgumentException();
                    } catch (Exception e) {
                        result.getErrors().add("Row " + lineNum + ": Invalid latitude (-90 to 90): " + latStr);
                        result.setSkippedCount(result.getSkippedCount() + 1);
                        continue;
                    }
                }

                if (!lngStr.isBlank()) {
                    try {
                        lng = Double.parseDouble(lngStr);
                        if (lng < -180.0 || lng > 180.0) throw new IllegalArgumentException();
                    } catch (Exception e) {
                        result.getErrors().add("Row " + lineNum + ": Invalid longitude (-180 to 180): " + lngStr);
                        result.setSkippedCount(result.getSkippedCount() + 1);
                        continue;
                    }
                }

                NoiseRecordRequest req = new NoiseRecordRequest();
                req.setLocationName(locationName);
                req.setZoneType(zoneType);
                req.setRecordDate(dateStr);
                req.setRecordTime(timeStr);
                req.setNoiseLevel(noiseLevel);
                req.setLatitude(lat);
                req.setLongitude(lng);
                req.setDescription(desc);

                NoiseRecord saved = noiseRecordService.createRecord(req);
                result.getImportedRecords().add(saved);
                result.setImportedCount(result.getImportedCount() + 1);
            }
        }

        return result;
    }
}
