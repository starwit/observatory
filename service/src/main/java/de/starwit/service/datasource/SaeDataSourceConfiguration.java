package de.starwit.service.datasource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaeDataSourceConfiguration {

    @Value("${saeInput.jdbcUrl}")
    private String jdbcUrl;

    @Value("${saeInput.username}")
    private String username;
    
    @Value("${saeInput.password}")
    private String password;

    @Value("${saeInput.detectionsTableName}")
    private String detectionsTableName;
    
    public String getJdbcUrl() {
        return jdbcUrl;
    }
    
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getDetectionsTableName() {
        return detectionsTableName;
    }

    public void setDetectionsTableName(String detectionsTableName) {
        this.detectionsTableName = detectionsTableName;
    }

}
