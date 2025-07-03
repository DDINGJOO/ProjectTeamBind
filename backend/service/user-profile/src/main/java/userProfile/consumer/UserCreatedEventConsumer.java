package userProfile.consumer;

import dataserializer.DataSerializer;
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


    @KafkaListener(
            topics = "user.created",
            groupId = "user-profile-consumer-group"
    )
    public void consume(String message) {
        try {
            log.info("Received raw UserCreatedEvent JSON: {}", message);

            // ① raw JSON을 UserCreatedEvent 객체로 바로 역직렬화
            UserCreatedEvent event = DataSerializer
                    .deserialize(message, UserCreatedEvent.class)
                    .orElseThrow(() -> new IllegalArgumentException("UserCreatedEvent 역직렬화 실패"));

            // ② 비즈니스 로직: UserProfile 생성
            UserProfile profile = UserProfile.builder()
                    .userId(event.getUserId())
                    .nickname("user_" + UUID.randomUUID().toString().substring(0, 6))
                    .email(event.getEmail())
                    .createdAt(LocalDateTime.now())
                    .build();
            userProfileRepository.save(profile);

            log.info("UserProfile 생성 완료 - userId: {}", event.getUserId());
        } catch (Exception e) {
            log.error("UserCreatedEvent 처리 실패: {}", e.getMessage(), e);
        }
    }
}
