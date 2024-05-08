package de.starwit.service.geojson;

import java.net.URI;
import java.time.Duration;

import org.geojson.GeoJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeoJsonSenderService {

    private static final Logger log = LoggerFactory.getLogger(GeoJsonSenderService.class.getName());

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${geojson.enabled:false}")
    private boolean geoJsonEnabled;

    @Value("${geojson.targetRestEndpointUrl:http://localhost:12345}")
    private URI GEO_JSON_URL;

    //TODO fix this
    @Value("${geojson.requestTimeoutMs}")
    private int requestTimeoutMs;

    public GeoJsonSenderService() {
        this.objectMapper = new ObjectMapper();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMillis(2000));
        
        this.restClient = RestClient.builder()
            .requestFactory(requestFactory)
            .build();
    }
    
    @Async("geoJsonExecutor")
    public void sendGeoJson(GeoJsonObject geoJson) {
        if (!geoJsonEnabled) {
            return;
        }

        try {
            String geoJsonString = objectMapper.writeValueAsString(geoJson);
            ResponseEntity<Void> response = restClient.post()
                .uri(GEO_JSON_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(geoJsonString)
                .retrieve().toBodilessEntity();
            
            log.info("GeoJson response code: {}", response.getStatusCode());
        } catch (JsonProcessingException e) {
            log.error("Failed to send GeoJson", e);
        }
    }
}
