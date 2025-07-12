package mail.consumer;

import dataserializer.DataSerializer;
import event.events.EmailConfirmedRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mail.service.MailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;




@Slf4j
@RequiredArgsConstructor
@Component
public class EmailConfirmedRequestEventConsumer {
    private final MailService mailService;

    @KafkaListener(
            topics = "email.confirmed.request",
            groupId = "email-consumer-group")
    public void consume(String message) {

        try {
            EmailConfirmedRequestEvent event = DataSerializer
                    .deserialize(message,EmailConfirmedRequestEvent.class)
                    .orElseThrow(() -> new IllegalArgumentException("UserCreatedEvent 역직렬화 실패"));


            mailService.confirmedEmail(event.getUserId(),event.getEmail(),event.getCode());
            log.info("이메일 확인 메일 발송 완료: {}", event.getUserId());
        }catch (Exception e){
            log.error("Failed to process LogEvent: {}", e.getMessage(), e);
        }
    }
}

