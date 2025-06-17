package inframessaging.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;



/**
 * Author : MyungJoo
 * Date : 2026/06/117
 */
@Data
@ConfigurationProperties(prefix = "bind.kafka.topic")
public class KafkaTopicProperties {
    private String userRegistered;
    private String orderCreated;
}