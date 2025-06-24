package auth.service.eventPublish;


import event.events.EmailConfirmedRequestEvent;
import event.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import outbox.publisher.OutboxEventPublisher;

@Service
@RequiredArgsConstructor
public class EventPublish {
    private final OutboxEventPublisher outboxEventPublisher;

    public void createUserEvent(Long userId, String email)
    {
        outboxEventPublisher.publish(
            UserCreatedEvent.builder()
                    .userId(userId)
                    .email(email)
                    .build()
                );
    }

    public void emailConfirmedEvent(Long userId, String email,String url)
    {
        outboxEventPublisher.publish(
                EmailConfirmedRequestEvent.builder()
                        .email(email)
                        .url(url)
                        .userId(userId)
                        .build()
        );
    }



}

