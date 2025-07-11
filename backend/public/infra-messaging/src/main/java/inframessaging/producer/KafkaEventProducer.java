package inframessaging.producer;

import event.CustomEvent;
import event.KafkaEventSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Author : MyungJoo
 * Date : 2026/06/117
 */
@Component
@RequiredArgsConstructor
public class KafkaEventProducer  {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String payload, String key) {
        kafkaTemplate.send(topic, key, payload);
    }



    public void send(CustomEvent event) {
        String json = KafkaEventSerializer.serialize(event);
        kafkaTemplate.send(event.getTopic(), json);
    }

}
