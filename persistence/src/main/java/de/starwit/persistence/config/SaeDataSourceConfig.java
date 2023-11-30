package de.starwit.persistence.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ComponentScan("de.starwit.analytics")
public class SaeDataSourceConfig {

    @Bean
    @ConfigurationProperties("sae.datasource")
    public DataSourceProperties saeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource saeDataSource() {
        return saeDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public JdbcTemplate saeJdbcTemplate(@Qualifier("saeDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}