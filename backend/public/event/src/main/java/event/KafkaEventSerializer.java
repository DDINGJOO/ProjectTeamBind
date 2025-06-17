package event;

import dataserializer.DataSerializer;

import java.time.Instant;


/**
 * Author : MyungJoo
 * Date : 2026/06/117
 */
public class KafkaEventSerializer {

    public static String serialize(CustomEvent event) {
        KafkaEventPayload payload = new KafkaEventPayload(
                event.name(),
                DataSerializer.serialize(event)
                        .orElseThrow(() -> new IllegalArgumentException("직렬화 실패: " + event.name())),
                Instant.now()
        );
        return DataSerializer.serialize(payload)
                .orElseThrow(() -> new IllegalArgumentException("KafkaEventPayload 직렬화 실패"));
    }

    public static KafkaEventPayload deserialize(String json) {
        return DataSerializer.deserialize(json, KafkaEventPayload.class)
                .orElseThrow(() -> new IllegalArgumentException("KafkaEventPayload 역직렬화 실패"));
    }
}

