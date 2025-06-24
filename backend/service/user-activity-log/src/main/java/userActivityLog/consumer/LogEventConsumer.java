package userActivityLog.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import dataserializer.DataSerializer;
import event.KafkaEventPayload;
import event.KafkaEventSerializer;
import logMetadata.LogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import primaryIdProvider.Snowflake;
import userActivityLog.entity.UserActivityLog;
import userActivityLog.repository.UserActivityLogRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogEventConsumer {

    private final UserActivityLogRepository repository;
    private final ObjectMapper objectMapper;
    private final Snowflake snowflake;

    @KafkaListener(topics = "activity.log.save", groupId = "log-consumer-group")
    public void consume(String message) {
        try {
            // 1) 래핑된 KafkaEventPayload 객체로 역직렬화
            KafkaEventPayload wrapper = KafkaEventSerializer.deserialize(message);

            // 2) 내부 data(JSON)만 꺼내서 LogEvent로 역직렬화
            String eventJson = wrapper.data();
            LogEvent event = DataSerializer
                    .deserialize(eventJson, LogEvent.class)
                    .orElseThrow(() -> new IllegalArgumentException("LogEvent 역직렬화 실패"));


            UserActivityLog log = UserActivityLog.builder()
                    .id(snowflake.nextId())
                    .userId(event.getUserId())
                    .actionType(event.getActionType().name())
                    .timestamp(event.getTimestamp())
                    .urlPath(event.getMetadata().getContext().get("urlPath")) // optional
                    .success(event.getMetadata().isSuccess())
                    .durationMillis(event.getMetadata().getDurationMillis())
                    .clientInfo(event.getMetadata().getClientInfo())
                    .failureReason(event.getMetadata().getFailureReason())
                    .extraPayload(objectMapper.writeValueAsString(event.getPayload()))
                    .build();
            repository.save(log);
        } catch (Exception e) {
            log.error("Failed to process LogEvent: {}", e.getMessage(), e);
        }
    }
}