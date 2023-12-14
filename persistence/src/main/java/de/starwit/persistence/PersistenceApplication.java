package de.starwit.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = { "de.starwit.persistence",
        "de.starwit.application.config" }, excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "de.starwit.persistence.sae.repository.*"))
public class PersistenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistenceApplication.class, args);
    }

}
