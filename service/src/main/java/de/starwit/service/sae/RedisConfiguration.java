package de.starwit.service.sae;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

@Configuration
public class RedisConfiguration {

    @Value("${sae.redisHost:localhost}")
    private String redisHost;

    @Value("${sae.redisPort:6379}")
    private int redisPort;
    
    @Bean
    StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer() {
        return StreamMessageListenerContainer.create(lettuceConnectionFactory());
    }

    @Bean
    LettuceConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory(
            new RedisStandaloneConfiguration(redisHost, redisPort), 
            LettuceClientConfiguration.builder().commandTimeout(Duration.ofDays(9999)).build());
    }
    
}
