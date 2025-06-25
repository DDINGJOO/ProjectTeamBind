package outbox.sender;


import inframessaging.producer.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import outbox.config.OutboxStatus;
import outbox.entity.OutboxEventEntity;
import outbox.repository.OutboxEventRepository;

import java.time.LocalDateTime;
import java.util.List;
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventRelayScheduler {

    private final OutboxEventRepository repository;
    private final KafkaEventProducer sender;

    @Scheduled(fixedDelay = 5000)
    public void relayEvents() {
        List<OutboxEventEntity> events = repository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxEventEntity event : events) {
            try {
                sender.send(event.getTopic(), event.getPayload(), null);
                event.markSuccess();
            } catch (Exception e) {
                log.warn("Failed to send event [id={}, topic={}]: {}", event.getId(), event.getTopic(), e.getMessage(), e);
                event.markFailure(e.getMessage());
            }
        }

        repository.saveAll(events);
    }

    @Scheduled(cron = "0 0 * * * *") // 매 정시마다
    public void deleteOldSentEvents() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
        int deletedCount = repository.deleteByStatusAndUpdatedAtBefore(OutboxStatus.SENT, cutoff);
        log.info("Deleted {} old sent events", deletedCount);
    }
}