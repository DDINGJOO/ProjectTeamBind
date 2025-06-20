package outbox.sender;

import inframessaging.producer.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import outbox.entity.OutboxEventEntity;
import outbox.repository.OutboxEventRepository;

import java.util.List;
@Slf4j
@Component
@RequiredArgsConstructor

public class OutboxEventRelayScheduler {

    private final OutboxEventRepository repository;
    private final KafkaEventProducer messagingSender;

    @Scheduled(fixedDelay = 1000)
    public void relay() {
        List<OutboxEventEntity> events = repository.findTop100BySentFalseOrderByCreatedAtAsc();

        for (OutboxEventEntity e : events) {
            if (!e.isReadyToSend()) continue;

            try {
                messagingSender.send(e.getType(), e.getPayload(), String.valueOf(e.getId()));
                e.markSuccess();
            } catch (Exception ex) {
                log.warn("[Outbox] Failed to send event: id={}, reason={}", e.getId(), ex.getMessage());
                e.markFailure(ex.getMessage());
            }
        }

        repository.saveAll(events);
    }
}
