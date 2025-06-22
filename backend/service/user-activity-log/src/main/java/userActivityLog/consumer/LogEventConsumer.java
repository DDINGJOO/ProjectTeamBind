package userActivityLog.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
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
            LogEvent event = objectMapper.readValue(message, LogEvent.class);

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