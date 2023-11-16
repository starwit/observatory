package de.starwit.persistence.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaeDataSourceConfig {
    
    @Bean
    @ConfigurationProperties("sae.datasource")
    public DataSourceProperties saeDataSourceProperties() {
        return new DataSourceProperties();
    }

    public DataSource saeDataSource() {
        return saeDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }
}