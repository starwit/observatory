package de.starwit.service.sae;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ClientOptions.DisconnectedBehavior;

@Configuration
public class RedisConfiguration {

        @Value("${spring.data.redis.host:localhost}")
        private String redisHost;

        @Value("${spring.data.redis.port:6379}")
        private int redisPort;

        @Value("${sae.redisStreamPrefix:output}")
        private String REDIS_STREAM_PREFIX;

        private Logger log = LoggerFactory.getLogger(this.getClass());

        @Value("${spring.data.redis.active:false}")
        private Boolean activateRedis;

        @Bean
        @ConditionalOnProperty(value = "spring.data.redis.active", havingValue = "true", matchIfMissing = false)
        LettuceConnectionFactory lettuceConnectionFactory() {
                RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
                ClientOptions options = ClientOptions.builder().autoReconnect(true)
                                .disconnectedBehavior(DisconnectedBehavior.REJECT_COMMANDS).build();
                LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                                .clientOptions(options)
                                .build();
                LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig,
                                clientConfig);
                factory.setShareNativeConnection(false);
                return factory;
        }

        @Bean
        @ConditionalOnProperty(value = "spring.data.redis.active", havingValue = "true", matchIfMissing = false)
        StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
                StringRedisTemplate template = new StringRedisTemplate();
                template.setConnectionFactory(connectionFactory);
                template.setKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(new StringRedisSerializer());
                return template;
        }

        @Bean
        @ConditionalOnProperty(value = "spring.data.redis.active", havingValue = "true", matchIfMissing = false)
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer() {
                return StreamMessageListenerContainer.create(lettuceConnectionFactory());
        }

}
