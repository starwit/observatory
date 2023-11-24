package de.starwit.service.datasource;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SaeDetectionRowMapper implements RowMapper<SaeDetectionDTO> {

    @Override
    public SaeDetectionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SaeDetectionDTO.from(rs);
    }

}
