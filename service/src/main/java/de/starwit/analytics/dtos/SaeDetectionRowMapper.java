package de.starwit.analytics.dtos;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SaeDetectionRowMapper implements RowMapper<SaeDetectionDto> {

    @Override
    public SaeDetectionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SaeDetectionDto.from(rs);
    }

}
