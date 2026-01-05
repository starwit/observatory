package de.starwit.service.sae;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.starwit.persistence.observatory.entity.ObservationJobEntity;
import de.starwit.service.observatory.ObservationJobService;

@Service
public class StreamSubscriptionService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Value("${spring.data.redis.active:false}")
    private Boolean activateRedis;

    @Value("${sae.redisStreamPrefix:output}")
    private String REDIS_STREAM_PREFIX;

    @Autowired
    SimpleMessageListener listener;

    @Autowired
    ObservationJobService observationJobService;

    private Set<String> subscribedStreams = new HashSet<>();

    @Autowired(required = false)
    StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer;

    @Scheduled(fixedDelay = 5000)
    public void detectStreamsAndResubscribe() {
        if (streamMessageListenerContainer == null || activateRedis == false)
            return;

        subscribeToStream();

        if (!streamMessageListenerContainer.isRunning()) {
            streamMessageListenerContainer.start();
        }
    }

    private void subscribeToStream() {
        Set<String> streamKeys = getAllStreamKeys();
        for (String key : streamKeys) {
            if (!subscribedStreams.contains(key)) {
                log.debug("Subscribing to " + key);
                StreamOffset<String> streamOffset = StreamOffset.create(key, ReadOffset.latest());
                streamMessageListenerContainer.receive(streamOffset, listener);
                subscribedStreams.add(key);
            }
        }
    }

    private Set<String> getAllStreamKeys() {
        Set<String> result = new HashSet<>();
        List<ObservationJobEntity> enabledJobEntities = observationJobService.findDistinctByEnabledTrue();
        enabledJobEntities.forEach(entity -> {
            if (redisTemplate.hasKey(REDIS_STREAM_PREFIX + ":" + entity.getCameraId())) {
                result.add(REDIS_STREAM_PREFIX + ":" + entity.getCameraId());
            }
        });
        return result;
    }
}
