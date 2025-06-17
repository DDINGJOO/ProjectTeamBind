package event;
import java.time.Instant;

/**
 * Author : MyungJoo
 * Date : 2026/06/117
 */
public record KafkaEventPayload(
        String type,        // topic name or event name
        String data,        // JSON serialized payload
        Instant occurredAt  // 이벤트 발생 시점
) {}