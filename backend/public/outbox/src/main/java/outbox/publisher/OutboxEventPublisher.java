package outbox.publisher;

import event.CustomEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import outbox.entity.OutboxEventEntity;
import outbox.repository.OutboxEventRepository;
import primaryIdProvider.Snowflake;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository repository;
    private final Snowflake snowflake;

    public void publish(CustomEvent event) {
        OutboxEventEntity entity = OutboxEventEntity.from(event, snowflake);
        repository.save(entity);
    }

    public void publishBatch(List<CustomEvent> events) {
        List<OutboxEventEntity> entities = events.stream()
                .map(event -> OutboxEventEntity.from(event, snowflake))
                .toList();

        repository.saveAll(entities);
    }
}