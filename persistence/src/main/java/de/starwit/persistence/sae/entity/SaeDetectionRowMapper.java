package de.starwit.persistence.sae.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SaeDetectionRowMapper implements RowMapper<SaeDetectionEntity> {

    @Override
    public SaeDetectionEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SaeDetectionEntity.from(rs);
    }

}
