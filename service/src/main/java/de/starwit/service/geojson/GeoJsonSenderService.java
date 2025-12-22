package de.starwit.service.geojson;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.geojson.GeoJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Service
public class GeoJsonSenderService {

    private static final Logger log = LoggerFactory.getLogger(GeoJsonSenderService.class.getName());

    private final RestClient restClient;
    private final JsonMapper mapper;

    @Value("${geojson.enabled:false}")
    private boolean geoJsonEnabled;

    @Value("${geojson.targetRestEndpointUrl:http://localhost:12345}")
    private URI GEO_JSON_URL;

    // TODO fix this
    @Value("${geojson.requestTimeoutMs:2000}")
    private int requestTimeoutMs;

    public GeoJsonSenderService() {
        this.mapper = new JsonMapper();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMillis(2000));

        this.restClient = RestClient.builder()
                .requestFactory(makeTlsErrorIgnoringRequestFactory())
                .build();
    }

    @Async("geoJsonExecutor")
    public void sendGeoJson(GeoJsonObject geoJson) {
        if (!geoJsonEnabled) {
            return;
        }

        try {
            String geoJsonString = mapper.writeValueAsString(geoJson);
            ResponseEntity<Void> response = restClient.post()
                    .uri(GEO_JSON_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(geoJsonString)
                    .retrieve().toBodilessEntity();

            log.info("GeoJson response code: {}", response.getStatusCode());
        } catch (JacksonException e) {
            log.error("Failed to send GeoJson", e);
        }
    }

    private ClientHttpRequestFactory makeTlsErrorIgnoringRequestFactory() {
        TrustStrategy acceptingTrustStrategy = (chain, authType) -> true;

        SSLContext sslContext = null;
        try {
            sslContext = SSLContextBuilder.create().loadTrustMaterial(acceptingTrustStrategy).build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            log.error("Failed to create SSL context", e);
            throw new RuntimeException("Failed to create SSL context", e);
        }

        SSLConnectionSocketFactory socketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .setHostnameVerifier(new NoopHostnameVerifier())
                .build();

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", socketFactory)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(
                socketFactoryRegistry);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectionRequestTimeout(Duration.ofMillis(2000));
        requestFactory.setConnectionRequestTimeout(Duration.ofMillis(2000));
        requestFactory.setHttpClient(httpClient);

        return requestFactory;
    }
}
