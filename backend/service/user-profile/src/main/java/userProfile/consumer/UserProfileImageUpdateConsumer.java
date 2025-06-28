package userProfile.consumer;

import dataserializer.DataSerializer;
import event.events.UserProfileImageUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import userProfile.entity.UserProfile;
import userProfile.repository.UserProfileRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserProfileImageUpdateConsumer {

    private final UserProfileRepository userProfileRepository;


    @KafkaListener(
            topics = "user.profile.image.update",
            groupId = "user-profile-consumer-group"
    )
    public void consume(String message) {
        try {
            log.info("Received raw UserCreatedEvent JSON: {}", message);

            // ① raw JSON을 UserCreatedEvent 객체로 바로 역직렬화
            UserProfileImageUpdateEvent event = DataSerializer
                    .deserialize(message, UserProfileImageUpdateEvent.class)
                    .orElseThrow(() -> new IllegalArgumentException("UserProfileImageUpdateEvent 역직렬화 실패"));

            // ② 비즈니스 로직: UserProfile 생성
            UserProfile profile = userProfileRepository.findById(event.getUserId()).orElseThrow();
            profile.setProfileImageUrl(event.getProfileImageUrl());
            userProfileRepository.save(profile);
            log.info("UserProfile 이미지 변경완료- userId: {}", event.getUserId());
        } catch (Exception e) {
            log.error("UserProfileImageUpdateEvent 처리 실패: {}", e.getMessage(), e);
        }
    }
}
