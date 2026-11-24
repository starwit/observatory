package de.starwit.service.sae;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Base64;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.protobuf.InvalidProtocolBufferException;

import de.starwit.visionapi.Sae.VideoFrame;
import de.starwit.visionapi.Sae.SaeMessage;

public class SaeMessageDecoderTest {

    private static final SaeMessage MSG = SaeMessage.newBuilder()
            .setFrame(VideoFrame.newBuilder().setSourceId("cam1").setTimestampUtcMs(1234567890123L))
            .build();

    @Test
    public void testDecodesRaw() throws Exception {
        SaeMessage decoded = SaeMessageDecoder.decode(Map.of("proto_data", MSG.toByteArray()));

        assertThat(decoded).isEqualTo(MSG);
    }

    @Test
    public void testDecodesBase64() throws Exception {
        byte[] b64 = Base64.getEncoder().encode(MSG.toByteArray());

        SaeMessage decoded = SaeMessageDecoder.decode(Map.of("proto_data_b64", b64));

        assertThat(decoded).isEqualTo(MSG);
    }

    @Test
    public void testRejectsMissingField() {
        assertThatThrownBy(() -> SaeMessageDecoder.decode(Map.of("other", new byte[] { 1 })))
                .isInstanceOf(InvalidProtocolBufferException.class);
    }

    @Test
    public void testRejectsInvalidBase64() {
        assertThatThrownBy(() -> SaeMessageDecoder.decode(Map.of("proto_data_b64", "not base64!".getBytes())))
                .isInstanceOf(InvalidProtocolBufferException.class);
    }
}
