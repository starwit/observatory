package de.starwit.service.sae;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;

import com.google.protobuf.InvalidProtocolBufferException;

import de.starwit.visionapi.Sae.SaeMessage;

public class SaeMessageListener implements StreamListener<String, MapRecord<String, String, String>> {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private BlockingQueue<SaeDetectionDto> inputQueue;
    
    public SaeMessageListener() {
        this.inputQueue = new ArrayBlockingQueue<>(1000);
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

        List<SaeDetectionDto> dtoList = SaeDetectionDto.from(saeMsg);
        
        int discardCount = 0;
        for (SaeDetectionDto dto : dtoList) {
            boolean success = this.inputQueue.offer(dto);
            if (!success) {
                discardCount++;
            }
        }
        if (discardCount > 0) {
            log.warn("Discarded {} messages", discardCount);
        }
    }

    public List<SaeDetectionDto> getBufferedMessages() {
        List<SaeDetectionDto> outputList = new ArrayList<>();
        this.inputQueue.drainTo(outputList);
        return outputList;
    }
    
}
