package de.starwit.service.datasource;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Configuration
public class EntityManagerFactoryConfig {

    @Value("${saeInput.jdbcUrl}")
    private String jdbcUrl;

    @Value("${saeInput.username}")
    private String username;
    
    @Value("${saeInput.password}")
    private String password;

    @Bean
    public EntityManagerFactory saeEntityManagerFactory() {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.put("javax.persistence.jdbc.url", jdbcUrl);
        properties.put("javax.persistence.jdbc.user", username);
        properties.put("javax.persistence.jdbc.password", password);
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        return Persistence.createEntityManagerFactory("de.starwit.service.datasource");
    }
    
}
