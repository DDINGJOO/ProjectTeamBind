package userProfile.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @KafkaListener(topics = "user.created", groupId = "user-profile-consumer-group")
    public void consume(String message) {

        try {
            UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);

            UserProfile profile = UserProfile.builder()
                    .userId(event.getUserId())
                    .nickname("user_" + UUID.randomUUID().toString().substring(0, 6))
                    .email(event.getEmail())
                    .createdAt(LocalDateTime.now())
                    .build();

            userProfileRepository.save(profile);
            log.info("UserProfile 생성 완료 - userId: {}", event.getUserId());
        }catch (Exception e){
            log.error("Failed to process LogEvent: {}", e.getMessage(), e);
        }
    }
}



