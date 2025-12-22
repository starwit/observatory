package de.starwit.persistence.analytics;

import java.util.Objects;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "de.starwit.persistence.analytics", entityManagerFactoryRef = "analyticsEntityManagerFactory", transactionManagerRef = "analyticsTransactionManager")
public class AnalyticsDataSourceConfig {

    @Bean
    @ConfigurationProperties("analytics.datasource")
    public DataSourceProperties analyticsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Value("${analytics.datasource.flyway.locations}")
    private String[] flywayLocations;

    @Value("${analytics.timescale.datasource.flyway.locations:#{null}}")
    private String[] flywayTimescaleLocations;

    @Bean
    public DataSource analyticsDataSource() {
        return analyticsDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public JdbcTemplate analyticsJdbcTemplate(@Qualifier("analyticsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean analyticsEntityManagerFactory(EntityManagerFactoryBuilder builder,
            Environment env) {
        Flyway.configure()
                .dataSource(analyticsDataSource())
                .locations(flywayLocations)
                .load()
                .migrate();
        if (flywayTimescaleLocations != null) {
            Flyway.configure()
                .dataSource(analyticsDataSource())
                .locations(flywayTimescaleLocations)
                .table("flyway_schema_history_timescale")
                .baselineOnMigrate(true)
                .load()
                .migrate();
        }
        return builder
                .dataSource(analyticsDataSource())
                .packages("de.starwit.persistence.analytics")
                .persistenceUnit("analytics")
                .build();
    }

    @Bean
    public PlatformTransactionManager analyticsTransactionManager(
            @Qualifier("analyticsEntityManagerFactory") LocalContainerEntityManagerFactoryBean factoryBean) {
        return new JpaTransactionManager(Objects.requireNonNull(factoryBean.getObject()));
    }

}