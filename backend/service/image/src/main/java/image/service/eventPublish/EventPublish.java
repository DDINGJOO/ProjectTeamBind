package image.service.eventPublish;


import event.events.UserProfileImageUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import outbox.publisher.OutboxEventPublisher;

@Service
@RequiredArgsConstructor
public class EventPublish {
    private final OutboxEventPublisher outboxEventPublisher;

    public void createUserEvent(Long userId, String url)
    {
        outboxEventPublisher.publish(
            UserProfileImageUpdateEvent.builder()
                    .userId(userId)
                    .profileImageUrl(url)
                    .build()
                );
    }
}

