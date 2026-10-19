package de.starwit.service.sae;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;

import de.starwit.service.jobs.areaoccupancy.AreaOccupancyRunner;
import de.starwit.service.jobs.flow.FlowRunner;
import de.starwit.service.jobs.linecrossing.LineCrossingRunner;
import de.starwit.visionapi.Sae.SaeMessage;
import jakarta.annotation.PostConstruct;

@Component
public class SimpleMessageListener
        implements StreamListener<String, MapRecord<String, String, String>> {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LineCrossingRunner lineCrossingRunner;

    @Autowired
    FlowRunner flowRunner;

    @Autowired
    AreaOccupancyRunner areaOccupancyRunner;

    @Value("${sae.monotonicityFilterEnabled:false}")
    private boolean monotonicityFilterEnabled;

    private final StreamMonotonicityFilter monotonicityFilter = new StreamMonotonicityFilter();

    @PostConstruct
    private void init() {
        log.info("Timestamp monotonicity filter enabled: {}", monotonicityFilterEnabled);
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {

        String streamKey = message.getStream();

        String b64Proto = message.getValue().get("proto_data_b64");
        SaeMessage saeMsg;

        try {
            saeMsg = SaeMessage.parseFrom(Base64.getDecoder().decode(b64Proto));
        } catch (InvalidProtocolBufferException e) {
            log.warn("Received invalid proto");
            return;
        }

        if (monotonicityFilterEnabled && !monotonicityFilter.accept(streamKey, saeMsg.getFrame().getTimestampUtcMs())) {
            return;
        }

        SaeMessageDto dto = SaeMessageDto.from(streamKey, saeMsg);

        try {
            flowRunner.handleMessage(dto);
            lineCrossingRunner.handleMessage(dto);
            areaOccupancyRunner.handleMessage(dto);
        } catch (Exception e) {
            log.error("Error processing SAE message", e);
        }
    }
}
