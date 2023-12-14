package de.starwit.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Base RestApplication
 *
 * Disable default HATEOAS with exclude
 * <code>org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration</code>
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = { "de.starwit.rest", "de.starwit.service", "de.starwit.persistence",
        "de.starwit.application.config" }, excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "de.starwit.persistence.sae.*"))
public class RestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

}
