package inframessaging.producer;

import event.CustomEvent;
import inframessaging.config.KafkaProducerConfig;
import inframessaging.properties.KafkaTopicProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;




/**
 * Author : MyungJoo
 * Date : 2026/06/117
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = KafkaEventProducerTest.TestConfig.class)
@TestPropertySource("classpath:application.yaml")
class KafkaEventProducerTest {

    @Autowired
    private KafkaEventProducer kafkaEventProducer;

    static class TestUserRegisteredEvent implements CustomEvent {
        private final String id = "user-123";
        private final String email = "test@example.com";

        @Override
        public String name() {
            return "user.registered";
        }

        public String getId() { return id; }
        public String getEmail() { return email; }
    }

    @Test
    void 전송_테스트() {
        TestUserRegisteredEvent event = new TestUserRegisteredEvent();
        kafkaEventProducer.send(event);
    }

    @Configuration
    @ComponentScan(basePackages = "inframessaging.producer")
    @Import(KafkaProducerConfig.class)
    @EnableConfigurationProperties(KafkaTopicProperties.class)
    static class TestConfig {
        // KafkaProducerConfig는 producerFactory, KafkaTemplate Bean 제공
        // KafkaEventProducer는 @Component로 등록된 대상
    }
}