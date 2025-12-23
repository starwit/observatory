package de.starwit.service.sae;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleMessageListener
        implements StreamListener<String, MapRecord<String, String, String>> {

    @Override
    public void onMessage(MapRecord<String, String, String> message) {

        try {
            System.out.println("Processing: ");

        } catch (Exception e) {
            // message stays pending for retry
            throw e;
        }
    }
}