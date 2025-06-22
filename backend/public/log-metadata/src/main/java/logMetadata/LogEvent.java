package logMetadata;

import event.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogEvent<T> implements CustomEvent {

    private Long userId;
    private LogActionType actionType;
    private LocalDateTime timestamp;
    private LogMetadata metadata;
    private T payload;

    @Builder(builderMethodName = "now")
    public static <T> LogEvent<T> now(Long userId, LogActionType actionType, LogMetadata metadata, T payload) {
        return new LogEvent<>(userId, actionType, LocalDateTime.now(), metadata, payload);
    }

    @Override
    public String name() {
        return actionType.name();
    }

    @Override
    public String getTopic() {
        return actionType.getTopic();
    }
}