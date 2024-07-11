package de.starwit.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.redis.testcontainers.RedisContainer;

import de.starwit.persistence.analytics.repository.LineCrossingRepository;
import de.starwit.pipeline.RedisConnectionNotAvailableException;
import de.starwit.pipeline.SaeWriter;
import de.starwit.testing.SaeDump;
import de.starwit.visionapi.Messages.SaeMessage;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @LocalServerPort
    private int appPort;

    @Autowired
    private LineCrossingRepository lineCrossingRepository;
    
    @Container
    static RedisContainer redisContainer = new RedisContainer("redis:7.0").withExposedPorts(6379);

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16").withExposedPorts(5432);

    @Container
    static PostgreSQLContainer<?> analyticsContainer = new PostgreSQLContainer<>("postgres:16").withExposedPorts(5432);
    
    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:" + postgresContainer.getMappedPort(5432) + "/");
        registry.add("spring.datasource.username", () -> postgresContainer.getUsername());
        registry.add("spring.datasource.password", () -> postgresContainer.getPassword());

        registry.add("analytics.datasource.url", () -> "jdbc:postgresql://localhost:" + analyticsContainer.getMappedPort(5432) + "/");
        registry.add("analytics.datasource.username", () -> analyticsContainer.getUsername());
        registry.add("analytics.datasource.password", () -> analyticsContainer.getPassword());
        
        registry.add("sae.redisPort", () -> redisContainer.getMappedPort(6379));
    }
    
    @Test
    public void test() throws IOException, RedisConnectionNotAvailableException, InterruptedException {
        
        // Set up line counting observation job through REST request
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String bodyJson = Files.readString(Paths.get("src/test/resources/job1.json"));
        HttpEntity<String> request = new HttpEntity<String>(bodyJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + appPort + "/databackend/api/observation-job", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        Thread.sleep(1000);

        // Read SAE dump into Redis (using SaeWriter and SaeDump from vision-lib)
        SaeDump saeDump = new SaeDump(Paths.get("src/test/resources/stream1.saedump"));
        try (SaeWriter saeWriter = new SaeWriter("localhost", redisContainer.getMappedPort(6379))) {
            for (SaeMessage msg : saeDump) {
                saeWriter.write("geomapper:" + msg.getFrame().getSourceId(), msg, 10000);
                Thread.sleep(20);
            }
        }
        saeDump.close();
        
        Thread.sleep(1000);

        assertThat(lineCrossingRepository.findFirst100()).hasSize(2);

        // TODO We cannot access any of the retrieved objects because of an obscure ClassCastException. Further investigation is needed...
        // assertThat(lineCrossingRepository.findFirst100())
        //      .extracting(e -> e.getCrossingTime(), e -> e.getObjectId(), e -> e.getDirection())
        //      .containsExactly(
        //          tuple("2024-07-05T13:55:40.223Z", "e48e1732c9003ff7a330cfbc871857f2", "in"),
        //          tuple("2024-07-05T13:55:35.115Z", "6e008c1e14ef3a79acee7d7a2418895f", "in")
        //      );
    }
}
