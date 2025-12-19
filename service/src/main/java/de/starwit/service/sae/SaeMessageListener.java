package de.starwit.service.sae;

import java.util.Base64;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;

import com.google.protobuf.InvalidProtocolBufferException;

import de.starwit.visionapi.Sae.SaeMessage;

public class SaeMessageListener implements StreamListener<String, MapRecord<String, String, String>> {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private Consumer<SaeMessageDto> messageCallback;

    public SaeMessageListener(Consumer<SaeMessageDto> messageCallback) {
        this.messageCallback = messageCallback;
    }
    
    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String b64Proto = message.getValue().get("proto_data_b64");
        SaeMessage saeMsg;

        try {
            saeMsg = SaeMessage.parseFrom(Base64.getDecoder().decode(b64Proto));
        } catch (InvalidProtocolBufferException e) {
            log.warn("Received invalid proto");
            return;
        }

        SaeMessageDto dto = SaeMessageDto.from(saeMsg);
        
        try {
            messageCallback.accept(dto);
        } catch (Exception e) {
            log.error("Error processing SAE message", e);
        }
    }
    
}
