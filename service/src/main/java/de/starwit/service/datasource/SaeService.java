package de.starwit.service.datasource;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SaeService {

    @Autowired
    @Qualifier("saeJdbcTemplate")
    JdbcTemplate saeJdbcTemplate;

    private Instant lastRetrievedTime;
    private String cameraId;
    private Integer detectionClassId;
    private String detectionTableName;

    public List<SaeDetectionDTO> getSomeReport() {
        return this.saeJdbcTemplate.query("SELECT * FROM " + detectionTableName + " t"
                + " WHERE t.CAPTURE_TS > ? AND t.CAMERA_ID = ? AND t.CLASS_ID = ?"
                + " ORDER BY t.CAPTURE_TS ASC", new SaeDetectionRowMapper(),
                lastRetrievedTime, cameraId, detectionClassId);
    }

}
