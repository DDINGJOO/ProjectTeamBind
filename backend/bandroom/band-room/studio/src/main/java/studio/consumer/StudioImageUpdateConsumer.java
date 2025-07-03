package studio.consumer;


import dataserializer.DataSerializer;
import event.events.ImageUpdateEvent.StudioImageUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import studio.entity.Image;
import studio.entity.Studio;
import studio.repository.StudioRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class StudioImageUpdateConsumer {
    private final StudioRepository studioRepository;

    @KafkaListener(
            topics = "studio.image.update",
            groupId = "studio-consumer-group"
    )
    public void consume(String message)
    {
        try{
            log.info("Received raw UserCreatedEvent JSON: {}", message);
            StudioImageUpdateEvent event = DataSerializer
                    .deserialize(message, StudioImageUpdateEvent.class)
                    .orElseThrow(() -> new IllegalArgumentException("StudioImageUpdateEvent 역직렬화 실패"));

            Studio studio = studioRepository.findById(event.getReferenceId()).orElseThrow(() -> new IllegalArgumentException("NOT FOUND STUDIO ID"));
            List<Image> images = new ArrayList<>();
            event.getImages().forEach(
                    (e,k)-> images.add(
                            Image.builder()
                                    .id(e)
                                    .url(k)
                                    .studio(studio)
                                    .build()
                    )
            );
            studio.setImages(images);
            studioRepository.save(studio);
        } catch (Exception e) {
            log.error("StudioImageUpdate 처리 실패: {}", e.getMessage(), e);
        }
    }
}

/*

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

 */
