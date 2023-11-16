package de.starwit.service.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaeDataSource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Connection dbConn;
    private PreparedStatement preparedStatement;
    
    private Instant lastRetrievedTime;

    private String cameraId;
    private Integer detectionClassId;

    public SaeDataSource(String cameraId, Integer detectionClassId) {
        this.cameraId = cameraId;
        this.detectionClassId = detectionClassId;
    }

    private boolean ensureConnection() {
        // if (this.dbConn == null) {
        //     try {
        //         this.dbConn = DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(), config.getPassword());
        //         this.preparedStatement = this.dbConn.prepareStatement("""
        //             SELECT * 
        //             FROM \s""" + config.getDetectionsTableName() + """
        //             \s t 
        //             WHERE t."CAPTURE_TS" > ? AND t."CAMERA_ID" = ? AND t."CLASS_ID" = ?
        //             ORDER BY t."CAPTURE_TS" ASC
        //             """);
        //         this.preparedStatement.setString(2, cameraId);
        //         this.preparedStatement.setInt(3, detectionClassId);
        //         log.info("Successfully connected to database at {}", config.getJdbcUrl());
        //         return true;
        //     } catch (SQLException ex) {
        //         log.error("Could not connect to database at {}", config.getJdbcUrl(), ex);
        //         this.close();
        //         return false;
        //     }
        // } else {
        //     return true;
        // }
        return false;
    }

    private void close() {
        if (this.dbConn != null) {
            try {
                this.dbConn.close();
            } catch (SQLException ex) {
                log.warn("Error closing SAE source connection", ex);
            }
            this.dbConn = null;
        }
    }

    public List<SaeDetectionDTO> getNewData() {
        if (!this.ensureConnection()) {
            lastRetrievedTime = null;
            return new ArrayList<>();
        }
        
        if (lastRetrievedTime == null) {
            lastRetrievedTime = Instant.now();
        }

        List<SaeDetectionDTO> newData = getDetectionsSince(lastRetrievedTime);
        if (newData.size() > 0) {
            lastRetrievedTime = newData.get(newData.size() - 1).getCaptureTs();
        }

        return newData;
    }

    private List<SaeDetectionDTO> getDetectionsSince(Instant since) {
        try {
            this.preparedStatement.setTimestamp(1, new Timestamp(since.toEpochMilli()));
            log.info(this.preparedStatement.toString());
            ResultSet rs = this.preparedStatement.executeQuery();
            List<SaeDetectionDTO> result = new ArrayList<>();

            while (rs.next()) {
                result.add(SaeDetectionDTO.from(rs));
            }

            log.info("Retrieved {} results from database", rs.getRow());
            
            return result;
        } catch (SQLException ex) {
            log.error("Error executing query", ex);
            this.close();
            return new ArrayList<>();
        }
    }

}
