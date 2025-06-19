package outbox.publisher;

import event.CustomEvent;
import lombok.RequiredArgsConstructor;
import outbox.entity.OutboxEventEntity;
import outbox.repository.OutboxEventRepository;
import primaryIdProvider.Snowflake;

@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository repository;
    private final Snowflake snowflake;

    public void publish(CustomEvent event) {
        OutboxEventEntity entity = OutboxEventEntity.from(event, snowflake);
        repository.save(entity);
    }
}
