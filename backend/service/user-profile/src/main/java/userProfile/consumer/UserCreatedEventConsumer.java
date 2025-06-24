package userProfile.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import dataserializer.DataSerializer;
import event.KafkaEventPayload;
import event.KafkaEventSerializer;
import event.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import userProfile.entity.UserProfile;
import userProfile.repository.UserProfileRepository;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Component
public class UserCreatedEventConsumer {

    private final UserProfileRepository userProfileRepository;
    private final ObjectMapper objectMapper;
    @KafkaListener(topics = "user.created", groupId

            = "user-profile-consumer-group")
    public void consume(String message) {
        try {
            // 1) 래핑된 페이로드(Java 객체)로 역직렬화
            KafkaEventPayload wrapper = KafkaEventSerializer.deserialize(message);

            // 2) wrapper.getPayload() (JSON 문자열)를 실제 이벤트로 역직렬화

            // data()로 실제 이벤트 JSON 문자열 얻기
            String eventJson = wrapper.data();

            // UserCreatedEvent로 역직렬화
            UserCreatedEvent event = DataSerializer.deserialize(
                    eventJson,
                    UserCreatedEvent.class
            ).orElseThrow(() -> new IllegalArgumentException("UserCreatedEvent 역직렬화 실패"));
            // 3) 비즈니스 로직
            UserProfile profile = UserProfile.builder()
                    .userId(event.getUserId())
                    .nickname("user_" + UUID.randomUUID().toString().substring(0, 6))
                    .email(event.getEmail())
                    .createdAt(LocalDateTime.now())
                    .build();
            userProfileRepository.save(profile);
            log.info("UserProfile 생성 완료 - userId: {}", event.getUserId());

        } catch (Exception e) {
            log.error("KafkaEventPayload 처리 실패: {}", e.getMessage(), e);
        }
    }
}



