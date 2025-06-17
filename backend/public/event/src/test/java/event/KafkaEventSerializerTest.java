package event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Author : MyungJoo
 * Date : 2026/06/117
 */
class KafkaEventSerializerTest {

    static class TestEvent implements CustomEvent {
        private final String message = "hello";

        @Override
        public String name() {
            return "test.event";
        }

        public String getMessage() {
            return message;
        }
    }

    @Test
    void 직렬화_역직렬화_정상작동() {
        // given
        TestEvent event = new TestEvent();

        // when
        String serialized = KafkaEventSerializer.serialize(event);
        KafkaEventPayload payload = KafkaEventSerializer.deserialize(serialized);

        // then
        assertThat(payload.type()).isEqualTo("test.event");
        assertThat(payload.data()).contains("hello");
        assertThat(payload.occurredAt()).isNotNull();
    }
}
