package de.starwit;

import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.util.Random;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "de.starwit")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
