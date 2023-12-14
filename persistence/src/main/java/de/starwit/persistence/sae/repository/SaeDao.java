package de.starwit.persistence.sae.repository;

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
import org.springframework.stereotype.Component;

import de.starwit.persistence.sae.entity.SaeDetectionEntity;
import de.starwit.persistence.sae.entity.SaeDetectionRowMapper;

@Component
public class SaeDao {

    private String getDetectionDataSql;

    @Autowired
    @Qualifier("saeJdbcTemplate")
    private JdbcTemplate saeJdbcTemplate;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public SaeDao(@Value("${sae.detection.tablename}") String hyperTableName) {
        this.getDetectionDataSql = "select * from " 
            + hyperTableName 
            + " where \"capture_ts\" > ? "
            + "and \"camera_id\" = ? "
            + "and \"class_id\" = ? "
            + "order by \"capture_ts\" ASC";
    }

    public List<SaeDetectionEntity> getDetectionData(Instant lastRetrievedTime, String cameraId,
            Integer detectionClassId) {
        try {
            LocalDateTime ldt = LocalDateTime.ofInstant(lastRetrievedTime, java.time.ZoneId.systemDefault());
            List<SaeDetectionEntity> result = saeJdbcTemplate.query(getDetectionDataSql,
                    new SaeDetectionRowMapper(), ldt, cameraId, detectionClassId);
            return result;
        } catch (Exception e) {
            log.error("Error in getDetectionData", e);
            return new ArrayList<>();
        }
    }

}
