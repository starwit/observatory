package de.starwit.persistence.sae;

import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
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
@EnableJpaRepositories(basePackages = "de.starwit.persistence.sae", entityManagerFactoryRef = "saeEntityManagerFactory", transactionManagerRef = "saeTransactionManager")
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

    @Bean
    public LocalContainerEntityManagerFactoryBean saeEntityManagerFactory(
            EntityManagerFactoryBuilder builder, Environment env) {
        return builder
                .dataSource(saeDataSource())
                .packages("de.starwit.persistence.sae")
                .persistenceUnit("sae")
                .build();
    }

    @Bean
    public PlatformTransactionManager saeTransactionManager(
            @Qualifier("saeEntityManagerFactory") LocalContainerEntityManagerFactoryBean factoryBean) {
        return new JpaTransactionManager(Objects.requireNonNull(factoryBean.getObject()));
    }
}