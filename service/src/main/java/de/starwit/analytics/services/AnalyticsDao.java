package de.starwit.analytics.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import de.starwit.analytics.dtos.SaeDetectionDto;
import de.starwit.analytics.dtos.SaeDetectionRowMapper;

@Repository
public class AnalyticsDao {

    @Autowired
    @Qualifier("saeJdbcTemplate")
    private JdbcTemplate saeJdbcTemplate;

    @Value("${sae.detectionsTableName}")
    private String detectionTableName;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public List<SaeDetectionDto> getDetectionData(Instant lastRetrievedTime, String cameraId,
            Integer detectionClassId) {
        try {
            LocalDateTime ldt = LocalDateTime.ofInstant(lastRetrievedTime, java.time.ZoneId.systemDefault());
            List<SaeDetectionDto> result = saeJdbcTemplate.query(
                    "select * from detection2 "
                            + "where \"CAPTURE_TS\" > ? "
                            + "and \"CAMERA_ID\" = ? "
                            + "and \"CLASS_ID\" = ?"
                            + "order by \"CAPTURE_TS\" ASC",
                    new SaeDetectionRowMapper(), ldt, cameraId, detectionClassId);
            log.info("count in getDetectingData {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("Error in getDetectionData", e);
            return new ArrayList<>();
        }
    }

}
