package de.starwit.service.geojson;

import java.net.URI;
import java.time.Duration;

import org.geojson.GeoJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeoJsonService {

    private static final Logger log = LoggerFactory.getLogger(GeoJsonService.class.getName());

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${geojson.enabled}")
    private boolean geoJsonEnabled;

    @Value("${geojson.targetRestEndpointUrl}")
    private URI GEO_JSON_URL;

    @Value("${geojson.requestTimeoutMs:2000}")
    private int requestTimeoutMs;

    public GeoJsonService() {
        this.objectMapper = new ObjectMapper();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMillis(requestTimeoutMs));
        
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
            restClient.post()
                .uri(GEO_JSON_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(geoJsonString)
                .retrieve();
        } catch (JsonProcessingException e) {
            log.error("Failed to send GeoJson", e);
        }
    }
}
